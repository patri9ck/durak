package controller

import model.*
import observer.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

case class GroupController() extends Observable {

  def chooseDefending(group: Group, defendingPlayer: Player): Group = {
    if (group.players.isEmpty) {
      return group
    }

    val defendingIndex = group.players.indexWhere(_ == defendingPlayer)

    if (defendingIndex == -1) {
      return group
    }

    val updatedPlayers = group.players.zipWithIndex.map { case (player, idx) =>
      if (idx == defendingIndex) {
        player.copy(turn = Turn.Defending)
      } else if (group.players.length == 2) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else if (idx == (defendingIndex - 1 + group.players.length) % group.players.length) {
        player.copy(turn = Turn.SecondlyAttacking)
      } else if (idx == (defendingIndex + 1) % group.players.length) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else {
        player.copy(turn = Turn.Watching)
      }
    }
    
    Group(updatedPlayers)
  }

  def chooseDefendingRandomly(group: Group): Group = {
    chooseDefending(group, Random.shuffle(group.players).head)
  }
}

object GroupController {
  def createGroup(cardAmount: Int, names: List[String]): Group = {
    val givenCards = ListBuffer[Card]()

    val players: List[Player] = names.map { name => {
      val player = PlayerController.getNewPlayer(name, cardAmount, givenCards.toList, Card.getRandomCards)

      givenCards ++= player.cards

      player
    }

    }

    Group(players)
  }
}




