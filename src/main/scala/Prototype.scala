import scala.collection.mutable.ListBuffer
import scala.io.StdIn
import scala.util.Random

/**
 * All display strings must have the same length.
 */
enum Value(val order: Int, val display: String):
  case Two extends Value(2, " 2")
  case Three extends Value(3, " 3")
  case Four extends Value(4, " 4")
  case Five extends Value(5, " 5")
  case Six extends Value(6, " 6")
  case Seven extends Value(7, " 7")
  case Eight extends Value(8, " 8")
  case Nine extends Value(9, " 9")
  case Ten extends Value(10, "10")
  case Jack extends Value(11, " J")
  case Queen extends Value(12, " Q")
  case King extends Value(13, " K")
  case Ace extends Value(14, " A")

enum Suit(val display: String):
  case Spades extends Suit("♠")
  case Hearts extends Suit("♥")
  case Diamonds extends Suit("♦")
  case Clubs extends Suit("♣")

case class Card(value: Value, suit: Suit)

def genRandValue(): Value = Value.values(Random.nextInt(Value.values.length))

def genRandSuit(): Suit = Suit.values(Random.nextInt(Suit.values.length))

def genRandCard(): Card = Card(genRandValue(), genRandSuit())

def genRandCards(n: Int) : List[Card] = List.fill(n)(genRandCard())

def genCardDisplay(card: Card) : List[String] = List(
  "┌" + "─" * 4 + "┐",
  "│" + card.suit.display + " " * 3 + "│",
  "│ " + card.value.display + " │",
  "│" + " " * 3 + card.suit.display + "│",
  "└" + "─" * 4 + "┘",
)

def genOrder(cards: List[Card]): String = {
  var orderStr = ""

  for (i <- cards.indices) {
    orderStr += (i + 1)

    if (i != cards.length - 1) {
      orderStr += " " * 6
    }
  }

  orderStr
}

def genCardsDisplay(cards: List[Card]) : List[String] = {
  val displayedCards = ListBuffer[List[String]]()

  for (card <- cards) {
    displayedCards += genCardDisplay(card)
  }

  displayedCards.toList.transpose.map(_.mkString(" "))
}

def genCardsDisplayWithOrd(cards: List[Card]) : List[String] = genOrder(cards) :: genCardsDisplay(cards)

def genToDef(cards: List[Card]) : List[String] = "Zu Verteidigen" :: genCardsDisplayWithOrd(cards)

def genDefended(defended: List[Card], usedToDef: List[Card]) : List[String] = "Verteidigt" :: genCardsDisplay(defended) ++ genCardsDisplay(usedToDef)

def genOwnCards(name: String, cards: List[Card]): List[String] = s"${name}, Deine Karten" :: genCardsDisplayWithOrd(cards)

def clearScreen(): Unit = {
  println("\n" * 100)
}

def askPickUp(): Boolean = {
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

def askForCard(prompt: String, cards: List[Card]): Option[Card] = {
  if (cards.isEmpty) {
    return None
  }

  while (true) {
    print(s"${prompt} (" + 1 + "-" + cards.length + "/[A]ufnehmen) ")

    val answer = StdIn.readLine();

    if (answer.equalsIgnoreCase("a")) {
      return None
    }

    val order = answer.toIntOption

    if (order.isDefined && order.get >= 1 && order.get <= cards.length) {
      return Some(cards(order.get - 1))
    }
  }

  None
}

def askForToDefCard(cards: List[Card]): Option[Card] = {
  askForCard("Welche Karte möchtest du verteidigen?", cards)
}

def askForOwnCard(cards: List[Card]): Option[Card] = {
  askForCard("Welche Karte möchtest du dafür nutzen?", cards)
}

@main
def main(): Unit = {
  val cardsToDef = genRandCards(5).to(ListBuffer)
  val ownCards = genRandCards(7).to(ListBuffer)
  val defended = genRandCards(3).to(ListBuffer)
  val usedForDef = genRandCards(1).to(ListBuffer)

  while (true) {
    if (cardsToDef.isEmpty) {
      return
    }

    genToDef(cardsToDef.toList).foreach(println)

    println("\n")

    genDefended(defended.toList, usedForDef.toList).foreach(println)

    println("\n")

    genOwnCards("Patrick", ownCards.toList).foreach(println)

    println("\n")

    if (askPickUp()) {
      return
    }

    val toDef = askForToDefCard(cardsToDef.toList);

    if (toDef.isEmpty) {
      return;
    }

    val own = askForOwnCard(ownCards.toList);

    if (own.isEmpty) {
      return;
    }

    ownCards -= own.get
    cardsToDef -= toDef.get

    defended.prepend(toDef.get)
    usedForDef.prepend(own.get)

    clearScreen()
  }
}

