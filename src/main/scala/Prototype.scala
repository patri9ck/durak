import card.{Card, getRandomCards}
import round.{Player, Round, Turn, getNewPlayer}
import tui.*

import scala.collection.mutable.ListBuffer

class Prototype(val toDefend: ListBuffer[Card], val defended: ListBuffer[Card], val used: ListBuffer[Card]) {
  def run(mappedPlayers: Map[Player, Turn]): Map[Player, Turn] = {
    if (mappedPlayers.size != 1) {
      return finish(mappedPlayers)
    }

    val player = mappedPlayers.head._1

    if (toDefend.isEmpty) {
      return finish(mappedPlayers)
    }

    getToDefendDisplay(toDefend.toList).foreach(println)

    println("\n")

    getDefendedDisplay(defended.toList, used.toList).foreach(println)

    println("\n")

    getOwnDisplay(player).foreach(println)

    println("\n")

    if (askForPickUp()) {
      return finish(mappedPlayers)
    }

    val toDef = askForDefend(toDefend.toList)

    if (toDef.isEmpty) {
      return finish(mappedPlayers)
    }

    val own = askForOwn(player.cards)

    if (own.isEmpty) {
      return finish(mappedPlayers)
    }

    val newPlayer = Player(player.name, player.cards.filterNot(_ == own.get))
    toDefend -= toDef.get

    defended.prepend(toDef.get)
    used.prepend(own.get)

    clearScreen()

    Map(newPlayer -> Turn.Defending)
  }

  private def finish(mappedPlayers: Map[Player, Turn]): Map[Player, Turn] = mappedPlayers.map {
    case (key, _) => key -> Turn.Finished
  }
}

@main
def main(): Unit = {
  val mappedPlayers = Map[Player, Turn](getNewPlayer("Patrick", 7, getRandomCards) -> Turn.Defending)

  Round(mappedPlayers).run(Prototype(getRandomCards(5).to(ListBuffer), getRandomCards(3).to(ListBuffer), getRandomCards(3).to(ListBuffer)).run)

}

