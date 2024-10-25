package prototype

import card.{Card, getRandomCards}
import round.*
import tui.*

import scala.collection.mutable.ListBuffer

class Prototype(val toDefend: ListBuffer[Card], val defended: ListBuffer[Card], val used: ListBuffer[Card]) {
  def start(group: Group): Group = {
    val player = group.players.head

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

    val newPlayer = Player(player.name, player.cards.filterNot(_ == own.get), Turn.Defending)
    toDefend -= toDef.get

    defended.prepend(toDef.get)
    used.prepend(own.get)

    clearScreen()

    Group(List(newPlayer))
  }

  private def stop(group: Group): Group = {
    val player = group.players.head

    Group(List(Player(player.name, List(), Turn.Defending)))
  }
}

@main
def main(): Unit = {
  val names = askForAmountAndPlayers()

  val group = askForDefendingPlayer(createGroup(1, names))

  val round = Round(group,
    Prototype(getRandomCards(5).to(ListBuffer), getRandomCards(3).to(ListBuffer), getRandomCards(3).to(ListBuffer)).start,
    group => group.players.head.cards.isEmpty,
    group => group.players.head)

  round.start()




}

