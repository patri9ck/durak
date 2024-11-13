package model

import scala.collection.mutable.ListBuffer

case class Group(players: List[Player], stack: List[Card])

object Group {
  def createGroup(cardAmount: Int, names: List[String]): Group = {
    val givenCards = ListBuffer[Card]()

    val players: List[Player] = names.map { name => {
      val player = Player.getNewPlayer(name, cardAmount, givenCards.toList, Card.getRandomCards)

      givenCards ++= player.cards

      player
    }

    }
  
    // To-Do: Create list with remaining cards for stack
    Group(players)
  }
}
