package controller.base

import controller.Controller
import model.*
import model.Turn.{Defending, FirstlyAttacking}
import observer.Observable

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
      Round(Turn.Watching, List(), List(), List(), None, false))
    
    notifySubscribers()
  }

  private def chooseDefending(index: Int): Unit = {
    require(status != null)

    if (index < 0 || index >= status.group.players.size) {
      return;
    }

    val updatedPlayers = status.group.players.zipWithIndex.map { case (player, idx) =>
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

    status = status.copy(group = status.group.copy(players = updatedPlayers), status.round.copy(turn = Turn.FirstlyAttacking))
  }

  override def chooseDefending(defending: Player): Unit = {
    require(status != null)
    
    if (status.group.players.isEmpty) {
      return
    }
    
    chooseDefending(status.group.players.indexWhere(_ == defending))

    notifySubscribers()
  }

  override def chooseDefending(): Unit = {
    require(status != null)
    
    chooseDefending(Random.shuffle(status.group.players).head)

    notifySubscribers()
  }

  private def drawFromStack(): Unit = {
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
  }


  private def finished(finished: Player): Unit = {
    require(status != null)
    require(finished.turn == Turn.Defending || finished.turn == Turn.FirstlyAttacking || finished.turn == Turn.SecondlyAttacking)
    
    if (status.group.stack.nonEmpty) {
      return
    }

    if (finished.cards.nonEmpty) {
      return;
    }

    val updatedPlayer = finished.copy(turn = Turn.Watching)

    val updatedPlayers = status.group.players.map { player => 
      if (player == finished) {
        updatedPlayer
      } else {
        player
      }
    }
    
    if (finished.turn == Turn.Defending) {
      status = status.copy(group = status.group.copy(players = updatedPlayers),
        round = status.round.copy(defended = List(), undefended = List(), used = List()))
      
      chooseDefending((status.group.players.indexOf(updatedPlayer) - 1 + status.group.players.size) % status.group.players.size)
    
      return;
    }
    
    if (finished.turn == Turn.FirstlyAttacking && byTurn(Turn.SecondlyAttacking).isEmpty || finished.turn == Turn.SecondlyAttacking) {
      status = status.copy(group = status.group.copy(players = updatedPlayers), round = status.round.copy(turn = Turn.Defending))
      
      return;
    }

    status = status.copy(group = status.group.copy(players = updatedPlayers), round = status.round.copy(turn = Turn.SecondlyAttacking))
  }
  
  override def canAttack(card: Card): Boolean = {
    require(status != null);

    status.round.used.isEmpty && status.round.defended.isEmpty && status.round.undefended.isEmpty
      || status.round.used.exists(_.rank == card.rank)
      || status.round.defended.exists(_.rank == card.rank)
      || status.round.undefended.exists(_.rank == card.rank)
  }

  override def denied(): Unit = {
    require(status != null)
    
    val attacking = byTurn(status.round.turn)
    
    if (attacking.isEmpty || attacking.get.turn != Turn.FirstlyAttacking && attacking.get.turn != Turn.SecondlyAttacking) {
      return;
    }
    
    if (attacking.get.turn == Turn.FirstlyAttacking) {
      status = status.copy(round = status.round.copy(denied = true))

      notifySubscribers()
      
      return
    } 

    if (status.round.denied) {
      status = status.copy(round = status.round.copy(defended = List(),
        undefended = List(),
        used = List(),
        denied = false))
      
      drawFromStack()
      
      chooseDefending((status.group.players.indexOf(attacking.get) - 1 + status.group.players.size) % status.group.players.size)
      
      notifySubscribers()
      
      return
    }

    status = status.copy(round = status.round.copy(turn = Turn.Defending))
    
    notifySubscribers()
  }

  override def pickup(): Unit = {
    require(status != null)

    val defending = byTurn(Turn.Defending)

    if (defending.isEmpty) {
      return;
    }

    val updatedPlayer = defending.get.copy(cards = defending.get.cards ++ status.round.used ++ status.round.defended ++ status.round.undefended)

    val updatedPlayers = status.group.players.map { player =>
      if (player == defending) {
        updatedPlayer
      } else {
        player
      }
    }

    status = status.copy(group = status.group.copy(players = updatedPlayers),
      round = status.round.copy(defended = List(), undefended = List(), used = List()))
    
    chooseDefending((status.group.players.indexOf(defending) - 1 + status.group.players.size) % status.group.players.size)
    
    notifySubscribers()
  }

  override def attack(card: Card): Unit = {
    require(status != null)
    require(status.round.turn == Turn.FirstlyAttacking || status.round.turn == Turn.SecondlyAttacking)

    if (!canAttack(card)) {
      return
    }

    val attacking = byTurn(status.round.turn)

    if (attacking.isEmpty || !attacking.get.cards.contains(card)) {
      return
    }

    val updatedPlayer = attacking.get.copy(cards = attacking.get.cards.filterNot(_ == card))

    val updatedPlayers = status.group.players.map { player =>
      if (player == attacking) {
        updatedPlayer
      } else {
        player
      }
    }

    if (attacking.get.turn == Turn.FirstlyAttacking) {
      status = status.copy(
        group = status.group.copy(players = updatedPlayers),
        round = status.round.copy(undefended = card :: status.round.undefended,
          turn = Turn.SecondlyAttacking,
          denied = false)
      )
    } else {
      status = status.copy(
        group = status.group.copy(players = updatedPlayers),
        round = status.round.copy(undefended = card :: status.round.undefended,
          turn = Turn.Defending)
      )
    }


    
    finished(updatedPlayer)
    
    notifySubscribers()
  }

  override def canDefend(used: Card, undefended: Card): Boolean = {
    require(status != null)
    
    !used.beats(undefended)
  }
  
  override def defend(used: Card, undefended: Card): Unit = {
    require(status != null)
    
    if (!canDefend(used, undefended)) {
      return
    }
    
    val defending = byTurn(Turn.Defending)
    
    if (defending.isEmpty || !defending.get.cards.contains(used)) {
      return
    }

    val updatedPlayer = defending.get.copy(cards = defending.get.cards.filterNot(_ == used))

    val updatedPlayers = status.group.players.map { player =>
      if (player == defending) {
        updatedPlayer
      } else {
        player
      }
    }
    
    status = status.copy(
      group = status.group.copy(players = updatedPlayers),
      round = status.round.copy(undefended = status.round.undefended.filterNot(_ == undefended),
        defended = undefended :: status.round.defended,
        used = used :: status.round.used)
    )
    
    if (status.round.undefended.isEmpty) {
      finished(updatedPlayer)
    }
    
    notifySubscribers()
  }
  
  
  override def byTurn(turn: Turn): Option[Player] = {
    require(status != null)
    
    status.group.players.find(_.turn == turn)
  }
}





