package view

import controller.Controller
import controller.base.BaseController
import model.*
import util.Observer

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

class Tui(val controller: Controller, val step: Boolean) extends Observer {

  controller.add(this)
  
  var countdown: () => Unit = countdownSeconds

  override def update(): Unit = {
    if (step) {
      askForStep() match
        case Step.Continue => continue()
        case Step.Undo => controller.undo()
        case Step.Redo => controller.redo()
    } else {
      continue()
    }
  }

  def continue(): Unit = {
    if (controller.status.turn == Turn.Uninitialized) {
      val playerAmount = askForPlayerAmount
      val cardAmount = askForCardAmount(playerAmount)

      controller.start(cardAmount, askForNames(playerAmount))
    } else if (controller.status.turn == Turn.Initialized) {
      val players = controller.status.players

      println("Als nächstes werden alle Karten gezeigt!")

      askForContinue()

      displayPlayerCards(players)

      askForAttackingPlayer(players) match {
        case Some(player) => controller.chooseAttacking(player)
        case None => controller.chooseAttacking()
      }
    } else {
      val player = controller.getPlayer

      if (player.nonEmpty) {
        lookAway(player.get)

        val undefended = controller.status.undefended
        val defended = controller.status.defended
        val used = controller.status.used

        getPlayersDisplay(controller.status.players).foreach(println)
        println(getStackDisplay(controller.status.stack))
        getTrumpDisplay(controller.status.trump).foreach(println)
        getRoundCardsDisplay(undefended, defended, used).foreach(println)
        getOwnDisplay(player.get).foreach(println)

        if (player.get.turn == Turn.FirstlyAttacking || player.get.turn == Turn.SecondlyAttacking) {
          askForAttack(player.get, defended, undefended, deny, attack)
        } else if (controller.status.turn == Turn.Defending) {
          askForDefend(player.get, used, undefended, pickUp, defend)
        }
      }
    }
  }

  def deny(): Unit = {
    clearScreen()
    controller.deny()
  }

  def attack(card: Card): Unit = {
    if (controller.canAttack(card)) {
      clearScreen()
      controller.attack(card)
    }
  }

  def pickUp(): Unit = {
    clearScreen()
    controller.pickUp()
  }

  def defend(used: Card, undefended: Card): Unit = {
    if (controller.canDefend(used, undefended)) {
      clearScreen()
      controller.defend(used, undefended)
    }
  }
  
  def askForStep(): Step = {
    while (true) {
      print("[C]ontinue/[U]ndo/[R]edo? ")

      StdIn.readLine().toLowerCase match {
        case "c" => return Step.Continue
        case "u" => return Step.Undo
        case "r" => return Step.Redo
        case _ =>
      }
    }

    Step.Continue
  }

  def displayPlayerCards(players: List[Player]): Unit = {
    players.foreach(player => {
      lookAway(player)
      getOwnDisplay(player).foreach(println)
      askForContinue()
      clearScreen()
    })
  }

  def getStackDisplay(stack: List[Card]): String = s"Stapel: ${stack.length}"

  def getTrumpDisplay(trump: Card): List[String] = "Trumpf" :: getCardDisplay(trump)

  def getLookAwayDisplay(player: Player): String = s"$player, Du bist dran. Alle anderen wegschauen!"

  def getCountdownDisplay(seconds: Int): List[String] = (1 to seconds).reverse.map(i => s"$i...").toList

  def getPlayersDisplay(players: List[Player]): List[String] = players.map(player => s"${player.turn}: $player (Karten: ${player.cards.length})")

  def getCardDisplay(card: Card): List[String] = card.toString.split("\n").toList

  def getRoundCardsDisplay(undefended: List[Card], defended: List[Card], used: List[Card]): List[String] =
    getUndefendedDisplay(undefended) ++ getDefendedDisplay(defended, used)

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
      return Nil
    }
    

    getCardsOrder(cards) :: getCardsDisplay(cards)
  }

  def getUndefendedDisplay(undefended: List[Card]): List[String] = {
    val display = getOrderedCardsDisplay(undefended)

    if (display.isEmpty) {
      return Nil
    }

    "Zu Verteidigen" :: display
  }

  def getDefendedDisplay(defended: List[Card], used: List[Card]): List[String] = {
    val display = getCardsDisplay(defended) ++ getCardsDisplay(used)

    if (display.isEmpty) {
      return Nil
    }

    "Verteidigt" :: display
  }

  def getOwnDisplay(player: Player): List[String] = {
    s"$player, Deine Karten" :: getOrderedCardsDisplay(player.cards)
  }

  def countdownSeconds(): Unit = {
    getCountdownDisplay(3).foreach(i => {
      println(i)
      Thread.sleep(1000)
    })
  }

  def lookAway(player: Player): Unit = {
    println(getLookAwayDisplay(player))
    countdown()
    clearScreen()
  }

  def clearScreen(): Unit = {
    println("\n" * 100)
  }

  def askForCardAmount(playerAmount: Int): Int = {
    val limit = 52 / playerAmount

    while (true) {
      print(s"Wie viele Karten soll jeder Spieler erhalten? (2-$limit) ")

      val amount = StdIn.readLine().toIntOption

      if (amount.nonEmpty && amount.get >= 2 && amount.get <= limit)
        return amount.get
    }

    6
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
      var name = ""

      while (name.isBlank || names.contains(name)) {
        print(s"Name von Spieler $i: ")

        name = StdIn.readLine()
      }

      names += name
    }

    names.toList
  }

  def askForContinue(): Unit = {
    while (true) {
      print("[W]eitermachen? ")

      if (StdIn.readLine().equalsIgnoreCase("w")) {
        return
      }
    }
  }

  def askForAttackingPlayer(players: List[Player]): Option[Player] = {
    while (true) {
      print(s"Welcher Spieler soll angreifen? (Name/[Z]ufällig) ")

      val name = StdIn.readLine()

      if (name.equalsIgnoreCase("z")) {
        return None
      }

      val filteredPlayers = players.filter(_.name.equalsIgnoreCase(name))

      if (filteredPlayers.nonEmpty) {
        return Some(filteredPlayers.head)
      }
    }

    None
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

  def askForAttack(attacking: Player, defended: List[Card], undefended: List[Card], canceled: () => Unit, chosen: Card => Unit): Unit = {
    while (true) {
      val card = askForCard("Mit welcher Karte möchtest du angreifen?", attacking.cards, defended.nonEmpty
        || undefended.nonEmpty)

      if (card.isEmpty) {
        canceled.apply()

        return
      }

      chosen.apply(card.get)

      println("Mit dieser Karte kannst du nicht angreifen.")
      
      return
    }
  }

  def askForDefend(defending: Player, used: List[Card], undefended: List[Card], canceled: () => Unit, chosen: (Card, Card) => Unit): Unit = {
    while (true) {
      val undefendedCard = askForCard("Welche Karte möchtest du verteidigen?", undefended, true)

      if (undefendedCard.isEmpty) {
        canceled.apply()
      }

      val usedCard = askForCard("Welche Karte möchtest du dafür nutzen?", defending.cards, true)

      if (usedCard.isEmpty) {
        canceled.apply()
      }

      chosen.apply(usedCard.get, undefendedCard.get)

      println("Mit dieser Karte kannst du nicht verteidigen.")
      
      return
    }
  }
}

