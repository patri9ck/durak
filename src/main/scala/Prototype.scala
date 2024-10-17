import card.getRandomCards
import player.{Player, PlayerState}
import tui.{askForDefend, askForOwn, askForPickUp, clearScreen, getDefendedDisplay, getOwnDisplay, getToDefendDisplay}

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{break, breakable}

@main
def main(): Unit = {
  val cardsToDef = getRandomCards(5).to(ListBuffer)
  val ownCards = getRandomCards(7).to(ListBuffer)
  val defended = getRandomCards(3).to(ListBuffer)
  val usedForDef = getRandomCards(3).to(ListBuffer)

  breakable {
    while (true) {
      if (cardsToDef.isEmpty) {
        break
      }

      getToDefendDisplay(cardsToDef.toList).foreach(println)

      println("\n")

      getDefendedDisplay(defended.toList, usedForDef.toList).foreach(println)

      println("\n")

      getOwnDisplay(Player("Patrick", ownCards.toList, PlayerState()), ownCards.toList).foreach(println)

      println("\n")

      if (askForPickUp()) {
        break
      }

      val toDef = askForDefend(cardsToDef.toList)

      if (toDef.isEmpty) {
        break
      }

      val own = askForOwn(ownCards.toList)

      if (own.isEmpty) {
        break
      }

      ownCards -= own.get
      cardsToDef -= toDef.get

      defended.prepend(toDef.get)
      usedForDef.prepend(own.get)

      clearScreen()
    }
  }
}

