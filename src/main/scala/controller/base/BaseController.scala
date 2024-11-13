package controller.base

import controller.Controller
import model.*
import observer.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

case class BaseController() extends Observable, Controller {

  var status: Status = _

  override def createStatus(amount: Int, names: List[String]): Unit = {
    val allCards = for {
      rank <- Rank.values
      suit <- Suit.values
    } yield Card(rank, suit)

    val (playersCards, remainingCards) = names.foldLeft((List[Player](), allCards.toList)) { case ((players, cards), name) =>
      val player = Player.getNewPlayer(name, amount, Nil, Card.getRandomCards)
      (players :+ player, cards.filterNot(player.cards.contains))
    }

    val randomIndex = Random.nextInt(remainingCards.size)
    val trump = remainingCards(randomIndex)

    status = Status(Group(playersCards, remainingCards.take(randomIndex) ++ remainingCards.drop(randomIndex + 1) :+ trump, trump),
      Round(Turn.Watching, List(), List(), List(), None, false, false))
    
    notifySubscribers()
  }

  override def chooseDefending(defending: Player): Unit = {
    require(status != null)
    
    if (status.group.players.isEmpty) {
      return
    }
    
    val defendingIndex = status.group.players.indexWhere(_ == defending)

    if (defendingIndex == -1) {
      return
    }

    val updatedPlayers = status.group.players.zipWithIndex.map { case (player, idx) =>
      if (idx == defendingIndex) {
        player.copy(turn = Turn.Defending)
      } else if (status.group.players.length == 2) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else if (idx == (defendingIndex - 1 + status.group.players.length) % status.group.players.length) {
        player.copy(turn = Turn.SecondlyAttacking)
      } else if (idx == (defendingIndex + 1) % status.group.players.length) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else {
        player.copy(turn = Turn.Watching)
      }
    }
    
    status = status.copy(group = status.group.copy(players= updatedPlayers), status.round.copy(turn = Turn.FirstlyAttacking))

    notifySubscribers()
  }

  override def chooseDefendingRandomly(): Unit = {
    require(status != null)
    
    chooseDefending(Random.shuffle(status.group.players).head)
  }

  override def drawFromStack(): Unit = {
    require(status != null)
    
    val orderedPlayers = {
      val startIndex = status.round.passed.map(status.group.players.indexOf).getOrElse(
        (status.group.players.indexWhere(_.turn == Turn.SecondlyAttacking) + 1) % status.group.players.size
      )

      status.group.players.drop(startIndex) ++ status.group.players.take(startIndex)
    }

    val (updatedPlayers, updatedStack) = orderedPlayers.foldLeft((List.empty[Player], status.group.stack)) {
      case ((playersAcc, stack), player) if player.cards.size < 6 =>
        val (toDraw, remainingStack) = stack.splitAt(6 - player.cards.size)

        (playersAcc :+ player.copy(cards = player.cards ++ toDraw), remainingStack)
      case ((playersAcc, stack), player) =>
        (playersAcc :+ player, stack)
    }

    status = status.copy(group = (status.group.copy(players = updatedPlayers, stack = updatedStack)))
    
    notifySubscribers()
  }


  override def removeIfWon(): Option[Player] = {
    require(status != null)
    
    val winningPlayer = status.group.players.find(_.cards.isEmpty)

    val updatedPlayers = status.group.players.map {
      case player if player.cards.isEmpty => player.copy(turn = Turn.Watching)
      case player => player
    }

    status = status.copy(group = status.group.copy(players = updatedPlayers))
    
    notifySubscribers()
    
    winningPlayer
  }
  
  override def canAttack(card: Card): Boolean = {
    
  }
  
  override def attack(card: Card): Unit = {
    
  }
  
  override def canDefend(card: Card): Boolean = {
    
  }
  
  override def defend(card: Card): Unit = {
    
  }
  
  
  
  override def defending(): Option[Player] = {
    require(status != null)
    
    status.group.players.find(_.turn == Turn.Defending)
  }
}





