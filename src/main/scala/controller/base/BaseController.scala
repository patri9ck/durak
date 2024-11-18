package controller.base

import controller.Controller
import model.*
import observer.Observable

import scala.util.Random

case class BaseController(var status: Status) extends Controller {

  def chooseAttacking(players: List[Player], index: Int): List[Player] = {
    require(index >= 0)
    require(index < players.size)

    players.zipWithIndex.map { case (player, idx) =>
      if (player.turn == Turn.Finished) {
        player
      } else {
        def findPreviousActive(currentIndex: Int, steps: Int): Int = {
          if (steps == 0) currentIndex
          else {
            val prevIndex = (currentIndex - 1 + players.length) % players.length
            if (players(prevIndex).turn == Turn.Finished)
              findPreviousActive(prevIndex, steps)
            else
              findPreviousActive(prevIndex, steps - 1)
          }
        }

        val firstlyAttackingIndex = index
        val defendingIndex = findPreviousActive(firstlyAttackingIndex, 1)
        val secondlyAttackingIndex = findPreviousActive(defendingIndex, 1)

        if (idx == firstlyAttackingIndex) {
          player.copy(turn = Turn.FirstlyAttacking)
        } else if (idx == defendingIndex) {
          player.copy(turn = Turn.Defending)
        } else if (idx == secondlyAttackingIndex) {
          player.copy(turn = Turn.SecondlyAttacking)
        } else {
          player.copy(turn = Turn.Watching)
        }
      }
    }
  }
  
  def chooseNextAttacking(players: List[Player], previous: Player): List[Player] = {
    chooseAttacking(players, (players.indexOf(previous) - 1 + players.size) % players.size)
  }

  override def chooseAttacking(attacking: Player): Unit = {
    status = status.copy(group = status.group.copy(players = chooseAttacking(status.group.players,
      status.group.players.indexWhere(_ == attacking))),
      round = status.round.copy(turn = Turn.FirstlyAttacking))

    notifySubscribers()
  }

  override def chooseAttacking(): Unit = {
    require(status.group.players.nonEmpty)
    
    chooseAttacking(Random.shuffle(status.group.players).head)
  }

  override def denied(): Unit = {
    requireAttack()
    requireTurn(Turn.FirstlyAttacking)

    val attacking = getPlayer.get

    if (status.round.denied || byTurn(Turn.SecondlyAttacking).isEmpty) {
      if (status.round.undefended.isEmpty) {
        drawFromStack()

        status = status.copy(
          group = status.group.copy(
            players = chooseNextAttacking(status.group.players, byTurn(Turn.FirstlyAttacking).get)
          ),
          round = status.round.copy(
            turn = Turn.FirstlyAttacking,
            defended = List(),
            undefended = List(),
            used = List(),
            denied = false))


      } else {
        status = status.copy(
          round = status.round.copy(
            turn = Turn.Defending))
      }
    } else {
      status = status.copy(round = status.round.copy(turn = Turn.SecondlyAttacking, denied = true))
    }
    
    notifySubscribers()
  }

  def updatePlayers(old: Player, updated: Player): List[Player] = {
    status.group.players.map { player =>
      if (old == player) {
        updated
      } else {
        player
      }
    }
  }

  override def pickUp(): Unit = {
    requireDefend()
    requireTurn(status.round.turn)

    val defending = getPlayer.get

    drawFromStack()

    val updated = defending.copy(cards = defending.cards ++ status.round.used ++ status.round.defended ++ status.round.undefended)
    
    var updatedPlayers = updatePlayers(defending, updated)
    
    updatedPlayers = chooseNextAttacking(updatedPlayers, updated)

    status = status.copy(group = status.group.copy(players = updatedPlayers),
      round = status.round.copy(turn = Turn.FirstlyAttacking, defended = List(), undefended = List(), used = List()))
    
    notifySubscribers()
  }

  def drawFromStack(): Unit = {
    requireTurn(Turn.FirstlyAttacking)

    val start = status.round.passed.orElse(byTurn(Turn.FirstlyAttacking)).map(status.group.players.indexOf).get

    var updatedStack = status.group.stack
    var updatedPlayers = status.group.players

    for (step <- status.group.players.indices) {
      val index = (start - step + status.group.players.size) % status.group.players.size
      val player = status.group.players(index)
      val amount = status.group.amount - player.cards.length

      if (player.turn != Turn.Watching && amount > 0) {
        val (draw, remaining) = updatedStack.splitAt(amount)

        updatedStack = remaining
        updatedPlayers = updatedPlayers.updated(index, player.copy(cards = player.cards ++ draw))
      }
    }

    status = status.copy(group = status.group.copy(players = updatedPlayers, stack = updatedStack))
  }
  
  override def canAttack(card: Card): Boolean = {
    status.round.defended.isEmpty && status.round.undefended.isEmpty
      || status.round.used.exists(_.rank == card.rank)
      || status.round.defended.exists(_.rank == card.rank)
      || status.round.undefended.exists(_.rank == card.rank)
  }

  private def requireTurn(turn: Turn): Unit = {
    require(byTurn(turn).nonEmpty)
  }

  override def getPlayer: Option[Player] = byTurn(status.round.turn)

  override def attack(card: Card): Unit = {
    requireAttack()
    requireTurn(status.round.turn)
    require(canAttack(card))

    val attacking = getPlayer.get

    val updated = attacking.copy(cards = attacking.cards.filterNot(_ == card))

    val updatedPlayers = updatePlayers(attacking, updated)

    val round = if (updated.turn == Turn.FirstlyAttacking && byTurn(Turn.SecondlyAttacking).isEmpty || updated.turn == Turn.SecondlyAttacking) {
      status.round.copy(undefended = card :: status.round.undefended,
        turn = Turn.Defending,
        denied = false)
    } else {
      status.round.copy(undefended = card :: status.round.undefended,
        turn = Turn.SecondlyAttacking,
        denied = false)
    }

    status = status.copy(
      group = status.group.copy(players = updatedPlayers),
      round = round
    )

    if (hasFinished(updated)) {
      finish(updated)
    }

    notifySubscribers()
  }

  def hasFinished(finished: Player): Boolean = {
    if (finished.turn == Turn.FirstlyAttacking || finished.turn == Turn.SecondlyAttacking) {
      return status.group.stack.isEmpty && finished.cards.isEmpty
    }

    if (finished.turn == Turn.Defending) {
      return status.group.stack.isEmpty && status.round.undefended.isEmpty && finished.cards.isEmpty
    }

    true
  }
  
  def finish(finished: Player): Unit = {
    val updated = finished.copy(turn = Turn.Finished)
    
    var updatedPlayers = updatePlayers(finished, updated)
    
    val round = if (finished.turn == Turn.Defending) {
      updatedPlayers = chooseNextAttacking(updatedPlayers, updated)
      
      status.round.copy(turn = Turn.FirstlyAttacking, defended = List(), undefended = List(), used = List())
    } else if (finished.turn == Turn.FirstlyAttacking && byTurn(Turn.SecondlyAttacking).isEmpty || finished.turn == Turn.SecondlyAttacking) {
      status.round.copy(turn = Turn.Defending)
    } else {
      status.round.copy(turn = Turn.SecondlyAttacking)
    }

    status = status.copy(group = status.group.copy(players = updatedPlayers), round = round)
  }

  override def byTurn(turn: Turn): Option[Player] = {
    status.group.players.find(_.turn == turn)
  }

  override def defend(used: Card, undefended: Card): Unit = {
    requireDefend()
    requireTurn(Turn.Defending)
    require(canDefend(used, undefended))

    val defending = byTurn(Turn.Defending).get

    val updated = defending.copy(cards = defending.cards.filterNot(_ == used))

    val updatedPlayers = updatePlayers(defending, updated)

    status = status.copy(group = status.group.copy(players = updatedPlayers), round = status.round.copy(turn = Turn.FirstlyAttacking,
      undefended = status.round.undefended.filterNot(_ == undefended),
      defended = undefended :: status.round.defended,
      used = used :: status.round.used))

    if (hasFinished(updated)) {
      finish(updated)
    }

    notifySubscribers()
  }

  override def canDefend(used: Card, undefended: Card): Boolean = {
    if (used.beats(undefended)) {
      return true
    }
    
    if (used.suit == status.group.trump.suit) {
      return undefended.suit != status.group.trump.suit
    }
    
    false
  }
  
  private def requireAttack(): Unit = {
    require(status.round.turn == Turn.FirstlyAttacking || status.round.turn == Turn.SecondlyAttacking)
  }
  
  private def requireDefend(): Unit = {
    require(status.round.turn == Turn.Defending)
  }
}





