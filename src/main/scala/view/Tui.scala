package view

import controller.Controller
import model.{Card, Rank, Status, Turn}
import observer.Observer

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

class Tui(val controller: Controller) extends Observer {

  controller.add(this)

  def update(): Unit = {
    val player = controller.byTurn(controller.status.round.turn)

    if (player.nonEmpty) {
      clearScreen()
      println(s"${player.get.name}, Du bist dran mit: ${controller.status.round.turn.name}! Alle anderen wegschauen!")
      countdown(3)
      clearScreen()

      getUndefendedDisplay.foreach(println)
      getDefendedDisplay.foreach(println)

      if (controller.status.round.turn == Turn.FirstlyAttacking || controller.status.round.turn == Turn.SecondlyAttacking) {
        getOwnDisplay.foreach(println)

        askForAttack()
      } else if (controller.status.round.turn == Turn.Defending) {
        getOwnDisplay.foreach(println)

        askForDefend()
      }
    }
  }

  def start(): Unit = {
    askForDefendingPlayer()
  }

  def countdown(seconds: Int): Unit = {
    for (i <- 1 until seconds + 1) {
      println(s"${i}...")
      Thread.sleep(1000L)
    }
  }

  def askForDefendingPlayer(): Unit = {
    while (true) {
      print(s"Welcher Spieler soll anfangen? (Name/[Z]ufällig) ")

      val name = StdIn.readLine()

      if (name.equalsIgnoreCase("z")) {
        controller.chooseDefending()
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

  def getOrderedCardsDisplay(cards: List[Card]): List[String] = {
    if (cards.isEmpty) {
      return List()
    }

    getCardsOrder(cards) :: getCardsDisplay(cards)
  }

  def getUndefendedDisplay: List[String] = {
    val display = getOrderedCardsDisplay(controller.status.round.undefended)

    if (display.isEmpty) {
      return Nil
    }

    "Zu Verteidigen" :: display
  }

  def getDefendedDisplay: List[String] = {
    val display = getCardsDisplay(controller.status.round.defended) ++ getCardsDisplay(controller.status.round.used)

    if (display.isEmpty) {
      return Nil
    }

    "Verteidigt" :: display
  }

  def getOwnDisplay: List[String] = {
    val player = controller.byTurn(controller.status.round.turn)

    if (player.isEmpty) {
      return Nil
    }

    s"${player.get.name}, Deine Karten" :: getOrderedCardsDisplay(player.get.cards)
  }

  def clearScreen(): Unit = {
    println("\n" * 100)
  }

  def askForCard(prompt: String, cards: List[Card], cancel: Boolean): Option[Card] = {
    while (true) {
      print(s"$prompt (" + 1 + "-" + cards.length + s"${if (cancel) "/[A]bbrechen" else ""}) ")

      val answer = StdIn.readLine()

      if (cancel && answer.equalsIgnoreCase("a")) {
        return None
      }

      val order = answer.toIntOption

      if (order.isDefined && order.get >= 1 && order.get <= cards.length) {
        return Some(cards(order.get - 1))
      }
    }

    None
  }

  def askForAttack(): Unit = {
    val attacking = controller.byTurn(controller.status.round.turn)

    if (attacking.isEmpty || attacking.get.turn != Turn.FirstlyAttacking && attacking.get.turn != Turn.SecondlyAttacking || attacking.get.cards.isEmpty) {
      return
    }

    while (true) {
      val card = askForCard("Mit welcher Karte möchtest du angreifen?", attacking.get.cards, controller.status.round.defended.nonEmpty
        || controller.status.round.undefended.nonEmpty)

      if (card.isEmpty) {
        controller.denied()

        return;
      }

      if (controller.canAttack(card.get)) {
        controller.attack(card.get)

        return
      }

      println("Mit dieser Karte kannst du nicht angreifen.")
    }
  }

  def askForDefend(): Unit = {
    val defending = controller.byTurn(controller.status.round.turn)

    if (defending.isEmpty || defending.get.turn != Turn.Defending || defending.get.cards.isEmpty) {
      return
    }

    while (true) {
      val undefended = askForCard("Welche Karte möchtest du verteidigen?", controller.status.round.undefended, true)

      if (undefended.isEmpty) {
        controller.pickUp()

        return;
      }

      val used = askForCard("Welche Karte möchtest du dafür nutzen?", defending.get.cards, true)

      if (used.isEmpty) {
        controller.pickUp()

        return
      }

      if (controller.canDefend(used.get, undefended.get)) {
        controller.defend(used.get, undefended.get)

        return
      }

      println("Mit dieser Karte kannst du nicht verteidigen.")
    }
  }
}

object Tui {
  def createStatus(): Status = {
    val playerAmount = askForPlayerAmount
    val cardAmount = askForCardAmount(playerAmount)

    Status.createStatus(cardAmount, askForNames(playerAmount))
  }

  def askForCardAmount(playerAmount: Int): Int = {
    val limit = 52 / playerAmount

    while (true) {
      print(s"Wie viele Karten soll jeder Spieler erhalten? (2-${limit}) ")

      val amount = StdIn.readLine().toIntOption

      if (amount.isDefined && amount.get >= 2 && amount.get <= limit) {
        return amount.get
      }
    }

    2
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
}

