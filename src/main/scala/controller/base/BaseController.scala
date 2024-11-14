package controller.base

import controller.Controller
import model.*
import model.Turn.{Defending, FirstlyAttacking}
import observer.Observable

import scala.util.Random

case class BaseController() extends Observable, Controller {

  var status: Status = _

  override def createStatus(amount: Int, names: List[String]): Unit = {
    val fullDeck: Array[Card] = for {
      suit <- Suit.values
      rank <- Rank.values
    } yield Card(rank, suit)

    val shuffledDeck = scala.util.Random.shuffle(fullDeck)

    val index = Random.nextInt(shuffledDeck.size)
    val trump = shuffledDeck(index)

    var remainingDeck = shuffledDeck.patch(index, Nil, 1)

    val players = names.map { name =>
      val playerCards = remainingDeck.take(amount)
      remainingDeck = remainingDeck.drop(amount)
      Player(name, playerCards.toList, Turn.Watching)
    }

    status = Status(
      Group(players, remainingDeck.toList, trump, amount),
      Round(Turn.Watching, List(), List(), List(), None, false)
    )

    notifySubscribers()
  }

  def chooseDefending(players: List[Player], index: Int): List[Player] = {
    require(index >= 0)
    require(index < players.size)

    players.zipWithIndex.map { case (player, idx) =>
      if (idx == index) {
        player.copy(turn = Turn.Defending)
      } else if (status.group.players.length == 2) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else if (idx == (index - 1 + status.group.players.length) % status.group.players.length) {
        player.copy(turn = Turn.SecondlyAttacking)
      } else if (idx == (index + 1) % status.group.players.length) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else {
        player.copy(turn = Turn.Watching)
      }
    }
  }
  
  def chooseNextDefending(players: List[Player], previous: Player): List[Player] = {
    chooseDefending(players, (players.indexOf(previous) - 1 + players.size) % players.size)
  }

  override def chooseDefending(defending: Player): Unit = {
    requireStatus()

    status = status.copy(group = status.group.copy(players = chooseDefending(status.group.players,
      status.group.players.indexWhere(_ == defending))),
      round = status.round.copy(turn = Turn.FirstlyAttacking))

    notifySubscribers()
  }

  override def chooseDefending(): Unit = {
    requireStatus()
    require(status.group.players.nonEmpty)
    
    chooseDefending(Random.shuffle(status.group.players).head)
  }

  def drawFromStack(): Unit = {
    requireStatus()
    
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
    requireStatus()
    
    status.group.players.map { player =>
      if (old == player) {
        updated
      } else {
        player
      }
    }
  }

  def hasFinished(finished: Player): Boolean = {
    requireStatus()
    
    (finished.turn == Turn.Defending || finished.turn == Turn.FirstlyAttacking || finished.turn == Turn.SecondlyAttacking)
      && status.group.stack.nonEmpty && finished.cards.nonEmpty
  }

  def handleFinish(finished: Player): Unit = {
    requireStatus()
    
    if (!hasFinished(finished)) {
      return;
    }
  
    val updated = finished.copy(turn = Turn.Watching)
    
    var updatedPlayers = updatePlayers(finished, updated)
    
    val round = if (finished.turn == Turn.Defending) {
      updatedPlayers = chooseDefending(updatedPlayers, (updatedPlayers.indexOf(updated) - 1 + updatedPlayers.size) % updatedPlayers.size)
      
      status.round.copy(turn = Turn.FirstlyAttacking, defended = List(), undefended = List(), used = List())
    } else if (finished.turn == Turn.FirstlyAttacking && byTurn(Turn.SecondlyAttacking).isEmpty || finished.turn == Turn.SecondlyAttacking) {
      status.round.copy(turn = Turn.Defending)
    } else {
      status.round.copy(turn = Turn.SecondlyAttacking)
    }

    status = status.copy(group = status.group.copy(players = updatedPlayers), round = round)
  }
  
  override def canAttack(card: Card): Boolean = {
    requireStatus()

    status.round.defended.isEmpty && status.round.undefended.isEmpty
      || status.round.used.exists(_.rank == card.rank)
      || status.round.defended.exists(_.rank == card.rank)
      || status.round.undefended.exists(_.rank == card.rank)
  }

  override def denied(): Unit = {
    requireAttack()
    require(status.round.defended.isEmpty && status.round.undefended.isEmpty)

    val attacking = byTurnThrow(status.round.turn)
    
    if (attacking.turn == Turn.FirstlyAttacking) {
      status = status.copy(round = status.round.copy(denied = true))
    } else if (status.round.denied) {
      status = status.copy(
        group = status.group.copy(
          players = chooseNextDefending(status.group.players, attacking)
        ),
        round = status.round.copy(defended = List(),
        undefended = List(),
        used = List(),
        denied = false))
      
      drawFromStack()
    } else {
      status = status.copy(round = status.round.copy(turn = Turn.Defending))
    }
    
    notifySubscribers()
  }

  override def pickUp(): Unit = {
    requireDefend()

    val defending = byTurnThrow(Turn.Defending)

    val updated = defending.copy(cards = defending.cards ++ status.round.used ++ status.round.defended ++ status.round.undefended)
    
    var updatedPlayers = updatePlayers(defending, updated)
    
    updatedPlayers = chooseNextDefending(updatedPlayers, updated)

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
      status.round.copy(undefended = card :: status.round.undefended,
        turn = Turn.SecondlyAttacking,
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
    require(status != null)
    
    used.beats(undefended)
  }
  
  override def defend(used: Card, undefended: Card): Unit = {
    requireDefend()
    
    if (!canDefend(used, undefended)) {
      return
    }
    
    val defending = byTurnThrow(Turn.Defending)

    val updated = defending.copy(cards = defending.cards.filterNot(_ == used))

    val updatedPlayers = updatePlayers(defending, updated)

    if (status.round.undefended.size == 1) {
      status = status.copy(group = status.group.copy(players = updatedPlayers), round = status.round.copy(turn = Turn.FirstlyAttacking,
        undefended = status.round.undefended.filterNot(_ == undefended),
        defended = undefended :: status.round.defended,
        used = used :: status.round.used))

      handleFinish(updated)
    } else {
      status = status.copy(group = status.group.copy(players = updatedPlayers), round = status.round.copy(undefended = status.round.undefended.filterNot(_ == undefended),
        defended = undefended :: status.round.defended,
        used = used :: status.round.used))
    }

    notifySubscribers()
  }
  
  private def requireStatus(): Unit = {
    require(status != null)
  }
  
  private def requireAttack(): Unit = {
    requireStatus()
    require(status.round.turn == Turn.FirstlyAttacking || status.round.turn == Turn.SecondlyAttacking)
  }
  
  private def requireDefend(): Unit = {
    requireStatus()
    require(status.round.turn == Turn.Defending)
  }
  
  private def byTurnThrow(turn: Turn): Player = {
    requireStatus()
    
    byTurn(turn).get
  }

  override def byTurn(turn: Turn): Option[Player] = {
    requireStatus()

    status.group.players.find(_.turn == turn)
  }
}





