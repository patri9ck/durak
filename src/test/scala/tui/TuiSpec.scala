// src/test/scala/tui/TuiSpec.scala
package tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import card.{Card, Rank, Suit}
import round.Player
import scala.io.StdIn
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class TuiSpec extends AnyWordSpec with Matchers {

  "TUI" should {

    "correctly display a card" in {
      val rank = Rank.Ace // Verwende den Ace-Rang aus dem Enum
      val suit = Suit.Spades // Verwende Pik (Spades) aus dem Enum

      val card = new Card(rank, suit)

      val cardDisplay = getCardDisplay(card)

      val expectedDisplay = List(
        "┌─────┐",
        "│A    │",
        "│  ♠  │",
        "│    A│",
        "└─────┘"
      )

      cardDisplay shouldEqual expectedDisplay
    }

    "correctly get the card order display" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val card1 = new Card(rank, suit)
      val card2 = new Card(rank, suit)

      val cards = List(card1, card2)

      val orderDisplay = getCardsOrder(cards)

      orderDisplay shouldEqual "1       2"
    }

    "correctly display multiple cards" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val card1 = new Card(rank, suit)
      val card2 = new Card(rank, suit)

      val cards = List(card1, card2)

      val cardsDisplay = getCardsDisplay(cards)

      val expectedDisplay = List(
        "┌─────┐ ┌─────┐",
        "│A    │ │A    │",
        "│  ♠  │ │  ♠  │",
        "│    A│ │    A│",
        "└─────┘ └─────┘"
      )

      cardsDisplay shouldEqual expectedDisplay
    }

    "correctly display ordered cards" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val card1 = new Card(rank, suit)
      val card2 = new Card(rank, suit)

      val cards = List(card1, card2)

      val orderedCardsDisplay = getOrderedCardsDisplay(cards)

      val expectedDisplay = List(
        "1       2",
        "┌─────┐ ┌─────┐",
        "│A    │ │A    │",
        "│  ♠  │ │  ♠  │",
        "│    A│ │    A│",
        "└─────┘ └─────┘"
      )

      orderedCardsDisplay shouldEqual expectedDisplay
    }

    "correctly display cards to defend" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val card1 = new Card(rank, suit)
      val card2 = new Card(rank, suit)

      val cards = List(card1, card2)

      val toDefendDisplay = getToDefendDisplay(cards)

      val expectedDisplay = List(
        "Zu Verteidigen",
        "1       2",
        "┌─────┐ ┌─────┐",
        "│A    │ │A    │",
        "│  ♠  │ │  ♠  │",
        "│    A│ │    A│",
        "└─────┘ └─────┘"
      )

      toDefendDisplay shouldEqual expectedDisplay
    }

    "correctly display defended cards" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val defendedCard = new Card(rank, suit)
      val usedCard = new Card(rank, suit)

      val defended = List(defendedCard)
      val used = List(usedCard)

      val defendedDisplay = getDefendedDisplay(defended, used)

      val expectedDisplay = List(
        "Verteidigt",
        "┌─────┐",
        "│A    │",
        "│  ♠  │",
        "│    A│",
        "└─────┘",
        "┌─────┐",
        "│A    │",
        "│  ♠  │",
        "│    A│",
        "└─────┘"
      )

      defendedDisplay shouldEqual expectedDisplay
    }

    "correctly display own cards" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val card1 = new Card(rank, suit)
      val card2 = new Card(rank, suit)

      val player = new Player("Max", List(card1, card2))

      val ownDisplay = getOwnDisplay(player)

      val expectedDisplay = List(
        "Max, Deine Karten",
        "1       2",
        "┌─────┐ ┌─────┐",
        "│A    │ │A    │",
        "│  ♠  │ │  ♠  │",
        "│    A│ │    A│",
        "└─────┘ └─────┘"
      )

      ownDisplay shouldEqual expectedDisplay
    }

    "clear screen by printing 100 new lines" in {
  val outContent = new ByteArrayOutputStream()
  Console.withOut(new PrintStream(outContent)) {
    clearScreen()
  }

  val output = outContent.toString.trim
  output shouldEqual ("\n" * 100).trim
}

    "ask for pick up and return true or false based on user input" in {
      val stdin = new ByteArrayInputStream("J\n".getBytes)
      Console.withIn(stdin) {
        askForPickUp() shouldEqual true
      }

      val stdin2 = new ByteArrayInputStream("N\n".getBytes)
      Console.withIn(stdin2) {
        askForPickUp() shouldEqual false
      }
    }

    "ask for a card and return the selected card or None" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val card1 = new Card(rank, suit)
      val cards = List(card1)

      val stdin = new ByteArrayInputStream("1\n".getBytes)
      Console.withIn(stdin) {
        askForCard("Wähle eine Karte", cards, true) shouldEqual Some(card1)
      }

      val stdin2 = new ByteArrayInputStream("A\n".getBytes)
      Console.withIn(stdin2) {
        askForCard("Wähle eine Karte", cards, true) shouldEqual None
      }
    }

    "ask for defend card and return the selected card or None" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val card1 = new Card(rank, suit)
      val cards = List(card1)

      val stdin = new ByteArrayInputStream("1\n".getBytes)
      Console.withIn(stdin) {
        askForDefend(cards) shouldEqual Some(card1)
      }

      val stdin2 = new ByteArrayInputStream("A\n".getBytes)
      Console.withIn(stdin2) {
        askForDefend(cards) shouldEqual None
      }
    }

    "ask for own card and return the selected card or None" in {
      val rank = Rank.Ace
      val suit = Suit.Spades

      val card1 = new Card(rank, suit)
      val cards = List(card1)

      val stdin = new ByteArrayInputStream("1\n".getBytes)
      Console.withIn(stdin) {
        askForOwn(cards) shouldEqual Some(card1)
      }

      val stdin2 = new ByteArrayInputStream("A\n".getBytes)
      Console.withIn(stdin2) {
        askForOwn(cards) shouldEqual None
      }
    }
  }
}
