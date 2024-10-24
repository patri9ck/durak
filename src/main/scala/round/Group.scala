package round

import card.{Card, getRandomCards}

import scala.collection.mutable.ListBuffer
import scala.util.Random

case class Group(players: List[Player]) {

  def chooseDefending(defendingPlayer: Player): Group = {
    if (players.isEmpty) {
      return this
    }

    val defendingIndex = players.indexWhere(_ == defendingPlayer)

    if (defendingIndex == -1) {
      return this
    }

    val updatedPlayers = players.zipWithIndex.map { case (player, idx) =>
      if (idx == defendingIndex) {
        player.copy(turn = Turn.Defending)
      } else if (players.length == 2) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else if (idx == (defendingIndex - 1 + players.length) % players.length) {
        player.copy(turn = Turn.SecondlyAttacking)
      } else if (idx == (defendingIndex + 1) % players.length) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else {
        player.copy(turn = Turn.Watching)
      }
    }
    
    Group(updatedPlayers)
  }

  def chooseDefendingRandomly(): Group = {
    chooseDefending(Random.shuffle(players).head)
  }
}

def createGroup(cardAmount: Int, names: List[String]): Group = {
  val givenCards = ListBuffer[Card]()

  val players: List[Player] = names.map { name => {
    val player = getNewPlayer(name, cardAmount, givenCards.toList, getRandomCards)

    givenCards ++= player.cards

    player
  }

  }
  Group(players)
}


