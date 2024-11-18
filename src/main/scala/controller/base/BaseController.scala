package controller.base

import controller.Controller
import model.*
import model.Turn.{Defending, FirstlyAttacking}
import observer.Observable

import scala.util.Random

case class BaseController(var status: Status) extends Controller {

  def chooseAttacking(players: List[Player], index: Int): List[Player] = {
    require(index >= 0)
    require(index < players.size)

    players.zipWithIndex.map { case (player, idx) =>
      if (idx == index) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else if (status.group.players.length == 2) {
        player.copy(turn = Turn.Defending)
      } else if (idx == (index - 1 + status.group.players.length) % status.group.players.length) {
        player.copy(turn = Turn.Defending)
      } else if (idx == (index + 1) % status.group.players.length) {
        player.copy(turn = Turn.SecondlyAttacking)
      } else {
        player.copy(turn = Turn.Watching)
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

  def drawFromStack(): Unit = {
    val orderedPlayers = {
      val startIndex = status.round.passed.map(status.group.players.indexOf).getOrElse(
        (status.group.players.indexWhere(_.turn == Turn.SecondlyAttacking) + 1) % status.group.players.size
      )

      status.group.players.drop(startIndex) ++ status.group.players.take(startIndex)
    }

    val (updatedPlayers, updatedStack) = orderedPlayers.foldLeft((List.empty[Player], status.group.stack)) {
      case ((playersAcc, stack), player) if player.cards.size < status.group.amount =>
        val (toDraw, remainingStack) = stack.splitAt(6 - player.cards.size)

        (playersAcc :+ player.copy(cards = player.cards ++ toDraw), remainingStack)
      case ((playersAcc, stack), player) =>
        (playersAcc :+ player, stack)
    }

    status = status.copy(group = (status.group.copy(players = updatedPlayers, stack = updatedStack)))
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

  def hasFinished(finished: Player): Boolean = {
    (finished.turn == Turn.Defending || finished.turn == Turn.FirstlyAttacking || finished.turn == Turn.SecondlyAttacking)
      && status.group.stack.isEmpty && finished.cards.isEmpty
  }

  def handleFinish(finished: Player): Boolean = {
    if (!hasFinished(finished)) {
      return false;
    }
  
    val updated = finished.copy(turn = Turn.Watching)
    
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

    true
  }
  
  override def canAttack(card: Card): Boolean = {
    status.round.defended.isEmpty && status.round.undefended.isEmpty
      || status.round.used.exists(_.rank == card.rank)
      || status.round.defended.exists(_.rank == card.rank)
      || status.round.undefended.exists(_.rank == card.rank)
  }

  override def denied(): Unit = {
    requireAttack()
    require(status.round.defended.nonEmpty || status.round.undefended.nonEmpty)

    val attacking = byTurnThrow(status.round.turn)

    if ((status.round.denied || byTurn(Turn.SecondlyAttacking).isEmpty) && !handleFinish(byTurnThrow(Turn.Defending))) {
      if (status.round.undefended.isEmpty) {
        drawFromStack()

        status = status.copy(
          group = status.group.copy(
            players = chooseNextAttacking(status.group.players, byTurnThrow(Turn.FirstlyAttacking))
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
    } else if (attacking.turn == Turn.FirstlyAttacking) {
      status = status.copy(round = status.round.copy(turn = Turn.SecondlyAttacking, denied = true))
    } else {
      status = status.copy(
        round = status.round.copy(
          turn = Turn.Defending))
    }
    
    notifySubscribers()
  }

  override def pickUp(): Unit = {
    requireDefend()

    val defending = byTurnThrow(Turn.Defending)

    drawFromStack()

    val updated = defending.copy(cards = defending.cards ++ status.round.used ++ status.round.defended ++ status.round.undefended)
    
    var updatedPlayers = updatePlayers(defending, updated)
    
    updatedPlayers = chooseNextAttacking(updatedPlayers, updated)

    status = status.copy(group = status.group.copy(players = updatedPlayers),
      round = status.round.copy(turn = Turn.FirstlyAttacking, defended = List(), undefended = List(), used = List()))
    
    notifySubscribers()
  }

  override def attack(card: Card): Unit = {
    requireAttack()

    if (!canAttack(card)) {
      return
    }

    val attacking = byTurnThrow(status.round.turn)

    val updated = attacking.copy(cards = attacking.cards.filterNot(_ == card))

    val updatedPlayers = updatePlayers(attacking, updated)

    val round = if (updated.turn == Turn.FirstlyAttacking) {
      val turn = if (byTurn(Turn.SecondlyAttacking).isEmpty) {
        Turn.Defending
      } else {
        Turn.SecondlyAttacking
      }

      status.round.copy(undefended = card :: status.round.undefended,
        turn = turn,
        denied = false)
    } else {
      status.round.copy(undefended = card :: status.round.undefended,
        turn = Turn.Defending)
    }

    status = status.copy(
      group = status.group.copy(players = updatedPlayers),
      round = round
    )

    handleFinish(updated)
    
    notifySubscribers()
  }

  override def canDefend(used: Card, undefended: Card): Boolean = {
    if (used.beats(undefended)) {
      return true
    }
    
    if (used.suit == status.group.trump.suit) {
      if (undefended.suit == status.group.trump.suit) {
        return used.beats(undefended)
      }
      
      return true
    }
    
    false
  }
  
  override def defend(used: Card, undefended: Card): Unit = {
    requireDefend()
    
    if (!canDefend(used, undefended)) {
      return
    }
    
    val defending = byTurnThrow(Turn.Defending)

    val updated = defending.copy(cards = defending.cards.filterNot(_ == used))

    val updatedPlayers = updatePlayers(defending, updated)

    if (status.round.undefended.size != 1 || !handleFinish(updated)) {
      status = status.copy(group = status.group.copy(players = updatedPlayers), round = status.round.copy(turn = Turn.FirstlyAttacking,
        undefended = status.round.undefended.filterNot(_ == undefended),
        defended = undefended :: status.round.defended,
        used = used :: status.round.used))
    }

    notifySubscribers()
  }

  override def byTurn(turn: Turn): Option[Player] = {
    status.group.players.find(_.turn == turn)
  }

  private def byTurnThrow(turn: Turn): Player = {
    byTurn(turn).get
  }
  
  private def requireAttack(): Unit = {
    require(status.round.turn == Turn.FirstlyAttacking || status.round.turn == Turn.SecondlyAttacking)
  }
  
  private def requireDefend(): Unit = {
    require(status.round.turn == Turn.Defending)
  }
}





