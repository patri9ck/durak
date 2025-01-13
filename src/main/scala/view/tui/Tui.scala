package view.tui

import com.google.inject.Inject
import controller.Controller
import model.*
import util.Observer
import view.tui.runner.Runner

import scala.collection.mutable.ListBuffer

class Tui(val controller: Controller, val runner: Runner, val seconds: Int, val lines: Int) extends Observer {

  @Inject()
  def this(controller: Controller, runner: Runner) = this(controller, runner, 3, 100)

  controller.add(this)

  private var controllable: Boolean = false

  override def update(): Unit = {
    runner.run(run)
  }

  def start(): Unit = {
    runner.run(() => {
      println("Willkommen zu Durak!")

      controllable = askForControllable()

      run()
    })
  }

  def run(): Unit = {
    if (controllable) {
      askForStep() match
        case Step.Continue => continue()
        case Step.Undo => controller.undo()
        case Step.Redo => controller.redo()
        case Step.Load => controller.load()
        case Step.Save => controller.save()
    } else {
      continue()
    }
  }

  def continue(): Unit = {
    if (controller.status.turn == Turn.Uninitialized) {
      val playerAmount = askForPlayerAmount
      val cardAmount = askForCardAmount(playerAmount)
      val names = askForNames(playerAmount)
      
      askForAttacking(names) match {
        case Some(name) => controller.initialize(cardAmount, names, name)
        case None => controller.initialize(cardAmount, names)
      }
    } else {
      val current = controller.current.get

      lookAway(current)

      val undefended = controller.status.undefended
      val defended = controller.status.defended
      val used = controller.status.used

      getPlayersDisplay(controller.status.players).foreach(println)
      println(getStackDisplay(controller.status.stack))
      getTrumpDisplay(controller.status.trump.get).foreach(println)
      getRoundCardsDisplay(undefended, defended, used).foreach(println)
      getOwnDisplay(current).foreach(println)

      if (current.turn == Turn.FirstlyAttacking || current.turn == Turn.SecondlyAttacking) {
        askForAttack(current, defended, undefended, deny, attack)
      } else if (controller.status.turn == Turn.Defending) {
        askForDefend(current, used, undefended, pickUp, defend)
      }
    }
  }

  def deny(): Unit = {
    println(getClearDisplay)
    controller.deny()
  }

  def attack(card: Card): Boolean = {
    if (controller.canAttack(card)) {
      println(getClearDisplay)
      controller.attack(card)

      return true
    }

    false
  }

  def pickUp(): Unit = {
    println(getClearDisplay)
    controller.pickUp()
  }

  def defend(used: Card, undefended: Card): Boolean = {
    if (controller.canDefend(used, undefended)) {
      println(getClearDisplay)
      controller.defend(used, undefended)

      return true
    }

    false
  }

  def countdown(): Unit = {
    getCountdownDisplay(seconds).foreach(i => {
      println(i)
      Thread.sleep(1000)
    })
  }

  def lookAway(player: Player): Unit = {
    println(getLookAwayDisplay(player))
    countdown()
    println(getClearDisplay)
  }
  
  def getClearDisplay: String = "\n" * lines

  def getStackDisplay(stack: List[Card]): String = s"Stapel: ${stack.length}"

  def getTrumpDisplay(trump: Card): List[String] = "Trumpf" :: getCardDisplay(trump)

  def getLookAwayDisplay(player: Player): String = s"$player, Du bist dran. Alle anderen wegschauen!"

  def getCountdownDisplay(seconds: Int): List[String] = (1 to seconds).reverse.map(i => s"$i...").toList

  def askForStep(): Step = {
    while (true) {
      runner.readLine("[F]ortfahren/[R]ückgängig machen/[W]iederherstellen/[L]aden/[S]peichern? ").toLowerCase match {
        case "f" => return Step.Continue
        case "r" => return Step.Undo
        case "w" => return Step.Redo
        case "l" => return Step.Load
        case "s" => return Step.Save
        case _ =>
      }
    }

    Step.Continue
  }

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

  def askForControllable(): Boolean = {
    while (true) {
      runner.readLine("Soll das Spiel steuerbar sein? (J/N) ").toLowerCase match {
        case "j" => return true
        case "n" => return false
        case _ =>
      }
    }

    false
  }

  def getPlayersDisplay(players: List[Player]): List[String] = players.map(player => s"${player.turn.name}: $player (Karten: ${player.cards.length})")

  def askForCardAmount(playerAmount: Int): Int = {
    val limit = 52 / playerAmount

    while (true) {
      val amount = runner.readLine(s"Wie viele Karten soll jeder Spieler erhalten? (2-$limit) ").toIntOption

      if (amount.nonEmpty && amount.get >= 2 && amount.get <= limit)
        return amount.get
    }

    6
  }

  def askForPlayerAmount: Int = {
    while (true) {
      val amount = runner.readLine("Wie viele Spieler sollen mitspielen? (Keine Doppelungen, mindestens 2) ").toIntOption

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
        name = runner.readLine(s"Name von Spieler $i: ")
      }

      names += name
    }

    names.toList
  }

  def askForContinue(): Unit = {
    while (true) {
      if (runner.readLine("Weitermachen? (J) ").equalsIgnoreCase("j")) {
        return
      }
    }
  }

  def askForAttacking(names: List[String]): Option[String] = {
    while (true) {
      val attacking = runner.readLine(s"Welcher Spieler soll angreifen? (Name/[Z]ufällig) ")

      if (attacking.equalsIgnoreCase("z")) {
        return None
      }

      val filteredNames = names.filter(_.equalsIgnoreCase(attacking))

      if (filteredNames.nonEmpty) {
        return Some(filteredNames.head)
      }
    }

    None
  }

  def askForCard(prompt: String, cards: List[Card], cancel: Boolean): Option[Card] = {
    while (true) {
      val answer = runner.readLine(s"$prompt (" + 1 + "-" + cards.length + s"${if (cancel) "/[A]bbrechen" else ""}) ")

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

  def askForAttack(attacking: Player, defended: List[Card], undefended: List[Card], canceled: () => Unit, chosen: Card => Boolean): Unit = {
    while (true) {
      val card = askForCard("Mit welcher Karte möchtest du angreifen?", attacking.cards, defended.nonEmpty
        || undefended.nonEmpty)

      if (card.isEmpty) {
        canceled.apply()

        return
      }

      if (chosen.apply(card.get)) {
        return
      }

      println("Mit dieser Karte kannst du nicht angreifen.")
    }
  }

  def askForDefend(defending: Player, used: List[Card], undefended: List[Card], canceled: () => Unit, chosen: (Card, Card) => Boolean): Unit = {
    while (true) {
      val undefendedCard = askForCard("Welche Karte möchtest du verteidigen?", undefended, true)

      if (undefendedCard.isEmpty) {
        canceled.apply()

        return
      }

      val usedCard = askForCard("Welche Karte möchtest du dafür nutzen?", defending.cards, true)

      if (usedCard.isEmpty) {
        canceled.apply()

        return
      }

      if (chosen.apply(usedCard.get, undefendedCard.get)) {
        return
      }

      println("Mit dieser Karte kannst du nicht verteidigen.")
    }
  }
}
