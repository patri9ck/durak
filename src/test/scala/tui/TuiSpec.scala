package tui

import card.{Card, Rank, Suit}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import round.{Group, Player, Turn}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class TuiSpec extends AnyWordSpec with Matchers {

  "TUI" should {

    "askForPlayerAmount" should {
      "return the valid player amount entered by the user" in {
        val input = new ByteArrayInputStream("abc\n1\n3\n".getBytes)
        Console.withIn(input) {
          askForPlayerAmount shouldEqual 3
        }
      }
    }

    "askForPlayers" should {
      "return a list of unique player names" in {
        val input = new ByteArrayInputStream("Alice\nBob\nAlice\nCharlie\n".getBytes)
        Console.withIn(input) {
          askForPlayers(3) shouldEqual List("Alice", "Bob", "Charlie")
        }
      }
    }

    "askForAmountAndPlayers" should {
      "prompt for player amount and then for player names" in {
        val input = new ByteArrayInputStream("2\nAlice\nBob\n".getBytes)
        Console.withIn(input) {
          askForAmountAndPlayers() shouldEqual List("Alice", "Bob")
        }
      }
    }

    "askForDefendingPlayer" should {
      "choose a specific player if the name is provided" in {
        val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Spades)), Turn.Watching)
        val player2 = Player("Bob", List(Card(Rank.King, Suit.Hearts)), Turn.Watching)
        val group = Group(List(player1, player2))

        val input = new ByteArrayInputStream("Alice\n".getBytes)
        Console.withIn(input) {
          askForDefendingPlayer(group) shouldEqual group.chooseDefending(player1)
        }
      }

      "choose a random player if 'Z' is entered" in {
        val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Spades)), Turn.Watching)
        val player2 = Player("Bob", List(Card(Rank.King, Suit.Hearts)), Turn.Watching)
        val group = Group(List(player1, player2))

        val input = new ByteArrayInputStream("Z\n".getBytes)
        Console.withIn(input) {
          val returnedGroup = askForDefendingPlayer(group)

          val defendingPlayer = returnedGroup.players.find(_.turn == Turn.Defending)

          defendingPlayer.get.name should (be(player1.name) or be(player2.name))
        }
      }

      "correctly display a card" in {
        val rank = Rank.Ace
        val suit = Suit.Spades

        val card = Card(rank, suit)

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

        val card1 = Card(rank, suit)
        val card2 = Card(rank, suit)

        val cards = List(card1, card2)

        val orderDisplay = getCardsOrder(cards)

        orderDisplay shouldEqual "1       2"
      }

      "correctly display multiple cards" in {
        val rank = Rank.Ace
        val suit = Suit.Spades

        val card1 = Card(rank, suit)
        val card2 = Card(rank, suit)

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

        val card1 = Card(rank, suit)
        val card2 = Card(rank, suit)

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

        val card1 = Card(rank, suit)
        val card2 = Card(rank, suit)

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

        val defendedCard = Card(rank, suit)
        val usedCard = Card(rank, suit)

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

        val card1 = Card(rank, suit)
        val card2 = Card(rank, suit)

        val player = Player("Max", List(card1, card2), Turn.Watching)

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

      "clear screen by printing 100 lines" in {
        val outContent = ByteArrayOutputStream()
        Console.withOut(PrintStream(outContent)) {
          clearScreen()
        }

        val output = outContent.toString.trim
        output shouldEqual ("\n" * 100).trim
      }

      "ask for pick up and return true or false based on user input" in {
        val stdin = ByteArrayInputStream("J\n".getBytes)
        Console.withIn(stdin) {
          askForPickUp() shouldEqual true
        }

        val stdin2 = ByteArrayInputStream("N\n".getBytes)
        Console.withIn(stdin2) {
          askForPickUp() shouldEqual false
        }
      }

      "ask for a card and return None when the cards list is empty" in {
        val emptyCards: List[Card] = List()

        val result = askForCard("Select a card", emptyCards, pickUp = false)

        result should be(None)
      }

      "ask for a card and not include 'Aufnehmen' in the prompt when pickUp is false" in {
        val card1 = Card(Rank.Ace, Suit.Spades)
        val card2 = Card(Rank.King, Suit.Hearts)
        val cards: List[Card] = List(card1, card2)

        val outputStream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(outputStream)) {
          val inputStream = new ByteArrayInputStream("1\n".getBytes)
          Console.withIn(inputStream) {
            askForCard("Select a card", cards, pickUp = false)
          }
        }

        val output = outputStream.toString

        output should not include "Aufnehmen"
        output should include("Select a card (1-2) ")

      }

      "ask for a card and return the selected card or None" in {
        val rank = Rank.Ace
        val suit = Suit.Spades

        val card1 = Card(rank, suit)
        val cards = List(card1)

        val stdin = ByteArrayInputStream("1\n".getBytes)
        Console.withIn(stdin) {
          askForCard("Wähle eine Karte", cards, true) shouldEqual Some(card1)
        }

        val stdin2 = ByteArrayInputStream("A\n".getBytes)
        Console.withIn(stdin2) {
          askForCard("Wähle eine Karte", cards, true) shouldEqual None
        }
      }

      "ask for defend card and return the selected card or None" in {
        val rank = Rank.Ace
        val suit = Suit.Spades

        val card1 = Card(rank, suit)
        val cards = List(card1)

        val stdin = ByteArrayInputStream("1\n".getBytes)
        Console.withIn(stdin) {
          askForDefend(cards) shouldEqual Some(card1)
        }

        val stdin2 = ByteArrayInputStream("A\n".getBytes)
        Console.withIn(stdin2) {
          askForDefend(cards) shouldEqual None
        }
      }

      "ask for own card and return the selected card or None" in {
        val rank = Rank.Ace
        val suit = Suit.Spades

        val card1 = Card(rank, suit)
        val cards = List(card1)

        val stdin = ByteArrayInputStream("1\n".getBytes)
        Console.withIn(stdin) {
          askForOwn(cards) shouldEqual Some(card1)
        }

        val stdin2 = ByteArrayInputStream("A\n".getBytes)
        Console.withIn(stdin2) {
          askForOwn(cards) shouldEqual None
        }
      }
    }
  }
}
