import card.{Card, getRandomCards}
import round.{Group, Player, Round, Turn, getNewPlayer}
import tui.*

import scala.collection.mutable.ListBuffer

class Prototype(val toDefend: ListBuffer[Card], val defended: ListBuffer[Card], val used: ListBuffer[Card]) {
  def start(group: Group): Group = {
    val player = group.mappedPlayers.head._1

    if (toDefend.isEmpty) {
      return stop(group)
    }

    getToDefendDisplay(toDefend.toList).foreach(println)

    println("\n")

    getDefendedDisplay(defended.toList, used.toList).foreach(println)

    println("\n")

    getOwnDisplay(player).foreach(println)

    println("\n")

    if (askForPickUp()) {
      return stop(group)
    }

    val toDef = askForDefend(toDefend.toList)

    if (toDef.isEmpty) {
      return stop(group)
    }

    val own = askForOwn(player.cards)

    if (own.isEmpty) {
      return stop(group)
    }

    val newPlayer = Player(player.name, player.cards.filterNot(_ == own.get))
    toDefend -= toDef.get

    defended.prepend(toDef.get)
    used.prepend(own.get)

    clearScreen()

    Group(Map(newPlayer -> Turn.Defending))
  }

  private def stop(group: Group): Group = {
    val player = group.mappedPlayers.head._1

    Group(Map(Player(player.name, List()) -> Turn.Defending))
  }
}

@main
def main(): Unit = {
  val group = Group(Map(getNewPlayer("Patrick", 5, getRandomCards) -> Turn.Defending))

  Round(group,
    Prototype(getRandomCards(5).to(ListBuffer), getRandomCards(3).to(ListBuffer), getRandomCards(3).to(ListBuffer)).start,
    group => group.mappedPlayers.head._1.cards.isEmpty,
    group => group.mappedPlayers.head._1).start()

}

