package view

import controller.Controller
import model.{Card, Group, Player, Rank}
import observer.Observer

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

class Tui(val controller: Controller) extends Observer {
  
  controller.add(this)
  
  def update(): Unit = {

  }
  
  def start() : Unit = {
    val amount = askForPlayerAmount

    controller.createStatus(amount, askForNames(amount))
    
    askForDefendingPlayer()
  }
  
  def askForPlayerAmount: Int = {
    while (true) {
      print("Wie viele Spieler sollen mitspielen? (Keine Doppelungen, mindestens 2) ")

      val amount = StdIn.readLine().toIntOption

      if (amount.isDefined && amount.get > 1) {
        return amount.get
      }
    }

    2
  }

  def askForNames(amount: Int): List[String] = {
    val names = ListBuffer[String]()

    for (i <- 1 until amount + 1) {
      var name = "";

      while (name.isBlank || names.contains(name)) {
        print(s"Name von Spieler ${i}: ")

        name = StdIn.readLine()
      }

      names += name
    }

    names.toList
  }

  def askForDefendingPlayer(): Unit = {
    while (true) {
      print(s"Welcher Spieler soll anfangen? (Name/[Z]ufällig) ")

      val name = StdIn.readLine()

      if (name.equalsIgnoreCase("z")) {
        controller.chooseDefendingRandomly()
      }

      val players = controller.status.group.players.filter(_.name.equalsIgnoreCase(name))

      if (players.nonEmpty) {
        controller.chooseDefending(players.head)
      }
    }
  }


  def getCardDisplay(card: Card): List[String] = {
    val biggestLength = Rank.getBiggestRankLength

    List(
      "┌" + "─" * (biggestLength * 2 + 1) + "┐",
      "│" + card.rank.display + " " * (2 * biggestLength + 1 - card.rank.display.length) + "│",
      "│" + " " * biggestLength + card.suit.display + " " * biggestLength + "│",
      "│" + " " * (2 * biggestLength + 1 - card.rank.display.length) + card.rank.display + "│",
      "└" + "─" * (biggestLength * 2 + 1) + "┘",
    )
  }

  def getCardsOrder(cards: List[Card]): String = {
    val biggestLength = Rank.getBiggestRankLength

    var orderStr = ""

    for (i <- cards.indices) {
      orderStr += (i + 1)

      if (i != cards.length - 1) {
        orderStr += " " * (2 * biggestLength + 3)
      }
    }

    orderStr
  }

  def getCardsDisplay(cards: List[Card]): List[String] = {
    val displayedCards = ListBuffer[List[String]]()

    for (card <- cards) {
      displayedCards += getCardDisplay(card)
    }

    displayedCards.toList.transpose.map(_.mkString(" "))
  }

  def getOrderedCardsDisplay(cards: List[Card]): List[String] = getCardsOrder(cards) :: getCardsDisplay(cards)

  def getToDefendDisplay(cards: List[Card]): List[String] = "Zu Verteidigen" :: getOrderedCardsDisplay(cards)

  def getDefendedDisplay(defended: List[Card], used: List[Card]): List[String] = "Verteidigt" :: getCardsDisplay(defended) ++ getCardsDisplay(used)

  def getOwnDisplay(player: Player): List[String] = s"${player.name}, Deine Karten" :: getOrderedCardsDisplay(player.cards)

  def clearScreen(): Unit = {
    println("\n" * 100)
  }

  def askForPickUp(): Boolean = {
    while (true) {
      print("Möchtest du aufnehmen? (J/N) ")

      val answer = StdIn.readLine()

      if (answer.equalsIgnoreCase("J")) {
        return true
      }

      if (answer.equalsIgnoreCase("N")) {
        return false
      }
    }

    false
  }

  def askForCard(prompt: String, cards: List[Card], pickUp: Boolean): Option[Card] = {
    if (cards.isEmpty) {
      return None
    }

    while (true) {
      print(s"$prompt (" + 1 + "-" + cards.length + s"${if (pickUp) "/[A]ufnehmen" else ""}) ")

      val answer = StdIn.readLine()

      if (pickUp && answer.equalsIgnoreCase("a")) {
        return None
      }

      val order = answer.toIntOption

      if (order.isDefined && order.get >= 1 && order.get <= cards.length) {
        return Some(cards(order.get - 1))
      }
    }

    None
  }

  def askForDefend(cards: List[Card]): Option[Card] = {
    askForCard("Welche Karte möchtest du verteidigen?", controller.status.round.undefended, true)
  }

  def askForOwn(cards: List[Card]): Option[Card] = {
    val defending = controller.defending()
    
    if (defending.isEmpty) {
      return None
    }

    askForCard("Welche Karte möchtest du dafür nutzen?", defending.get.cards, true)
  }

}


