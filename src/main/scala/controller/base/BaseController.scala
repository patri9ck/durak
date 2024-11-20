package controller.base

import controller.Controller
import model.*
import observer.Observable

import scala.annotation.tailrec
import scala.util.Random

case class BaseController(var status: Status) extends Controller {

  def chooseAttacking(players: List[Player], index: Int): List[Player] = {
    require(index >= 0)
    require(index < players.size)

    players.zipWithIndex.map { case (player, idx) =>
      if (player.turn == Turn.Finished) {
        player
      } else {
        @tailrec
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
    status = StatusBuilder.create(status)
      .setPlayers(chooseAttacking(status.group.players, status.group.players.indexWhere(_ == attacking)))
      .setTurn(Turn.FirstlyAttacking)
      .status

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
    
    val statusBuilder = StatusBuilder.create(status)

    if (status.round.denied || byTurn(Turn.SecondlyAttacking).isEmpty) {
      if (status.round.undefended.isEmpty) {
        drawFromStack(statusBuilder)
        
        statusBuilder
          .setPlayers(chooseNextAttacking(status.group.players, byTurn(Turn.FirstlyAttacking).get))
          .setTurn(Turn.FirstlyAttacking)
          .resetRound
      } else {
        statusBuilder
          .setTurn(Turn.Defending)
      }
    } else {
      statusBuilder
        .setTurn(Turn.SecondlyAttacking)
        .setDenied(true)
    }
    
    status = statusBuilder.status
    
    notifySubscribers()
  }

  def updatePlayers(players: List[Player], old: Player, updated: Player): List[Player] = {
    players.map { player =>
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
    
    val statusBuilder = StatusBuilder.create(status)

    drawFromStack(statusBuilder)

    val updated = defending.copy(cards = defending.cards ++ status.round.used ++ status.round.defended ++ status.round.undefended)
    
    status = statusBuilder
      .setPlayers(chooseNextAttacking(updatePlayers(status.group.players, defending, updated), updated))
      .resetRound
      .status
    
    notifySubscribers()
  }

  def drawFromStack(statusBuilder: StatusBuilder): Unit = {
    val start = statusBuilder.passed.orElse(statusBuilder.byTurn(Turn.FirstlyAttacking)).map(statusBuilder.players.indexOf).get

    var updatedStack = statusBuilder.stack
    var updatedPlayers = statusBuilder.players

    for (step <- statusBuilder.players.indices) {
      val index = (start - step + statusBuilder.players.size) % statusBuilder.players.size
      val player = statusBuilder.players(index)
      val amount = statusBuilder.amount - player.cards.length

      if (player.turn != Turn.Watching && amount > 0) {
        val (draw, remaining) = updatedStack.splitAt(amount)

        updatedStack = remaining
        updatedPlayers = updatedPlayers.updated(index, player.copy(cards = player.cards ++ draw))
      }
    }
    
    statusBuilder
      .setStack(updatedStack)
      .setPlayers(updatedPlayers)
      .removePassed()
      .status
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

    val statusBuilder = StatusBuilder.create(status)
      .setPlayers(updatePlayers(status.group.players, attacking, updated))
      .setUndefended(card :: status.round.undefended)
      .setDenied(false)
    
    if (updated.turn == Turn.FirstlyAttacking && byTurn(Turn.SecondlyAttacking).isEmpty || updated.turn == Turn.SecondlyAttacking) {
      statusBuilder.setTurn(Turn.Defending)
    } else {
      statusBuilder.setTurn(Turn.SecondlyAttacking)
    }
    
    if (hasFinished(updated, statusBuilder)) {
      finish(updated, statusBuilder)
    }

    status = statusBuilder.status

    notifySubscribers()
  }

  def hasFinished(finished: Player, statusBuilder: StatusBuilder): Boolean = {
    if (finished.turn == Turn.FirstlyAttacking || finished.turn == Turn.SecondlyAttacking) {
      return statusBuilder.stack.isEmpty && finished.cards.isEmpty
    }

    if (finished.turn == Turn.Defending) {
      return statusBuilder.stack.isEmpty && statusBuilder.undefended.isEmpty && finished.cards.isEmpty
    }

    true
  }
  
  def finish(finished: Player, statusBuilder: StatusBuilder): Unit = {
    val updated = finished.copy(turn = Turn.Finished)
    
    statusBuilder.setPlayers(updatePlayers(statusBuilder.players, finished, updated))

    if (finished.turn == Turn.Defending) {
      statusBuilder
        .setPlayers(chooseNextAttacking(statusBuilder.players, updated))
        .resetRound
    } else if (finished.turn == Turn.FirstlyAttacking && statusBuilder.byTurn(Turn.SecondlyAttacking).isEmpty || finished.turn == Turn.SecondlyAttacking) {
      statusBuilder.setTurn(Turn.Defending)
    } else {
      statusBuilder.setTurn(Turn.SecondlyAttacking)
    }
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
    
    val statusBuilder = StatusBuilder.create(status)
      .setPlayers(updatePlayers(status.group.players, defending, updated))
      .setUndefended(status.round.undefended.filterNot(_ == undefended))
      .setDefended(undefended :: status.round.defended)
      .setUsed(used :: status.round.used)
      .setTurn(Turn.FirstlyAttacking)

    if (hasFinished(updated, statusBuilder)) {
      finish(updated, statusBuilder)
    }
    
    status = statusBuilder.status

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





