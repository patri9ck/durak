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

      "loop twice before selecting a player" in {
        // Mocking the Group and Players
        val mockPlayer1 = Player("Alice", List(Card(Rank.Ace, Suit.Spades)), Turn.Watching)
        val mockPlayer2 = Player("Bob", List(Card(Rank.King, Suit.Hearts)), Turn.Watching)
        val group = Group(List(mockPlayer1, mockPlayer2))

        // Prepare simulated input and output streams
        val input = "Charlie\nAlice\n" // Simulated inputs (newline separated)
        val inputStream = new ByteArrayInputStream(input.getBytes)
        val outputStream = new ByteArrayOutputStream()

        // Redirect Console input and output
        Console.withIn(inputStream) {
          Console.withOut(new PrintStream(outputStream)) {
            // Call the method
            val defendingGroup = askForDefendingPlayer(group)

            // Assertions
            defendingGroup shouldBe group.chooseDefending(mockPlayer1) // Should choose Alice

            // Optionally check printed output
            val output = outputStream.toString
            output should include("Welcher Spieler soll anfangen?") // Check if prompt is shown
          }
        }
      }


    }
    "getCardDisplay" should {
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
    }

    "getCardsOrder" should {
      "correctly get the card order display" in {
        val rank = Rank.Ace
        val suit = Suit.Spades

        val card1 = Card(rank, suit)
        val card2 = Card(rank, suit)

        val cards = List(card1, card2)

        val orderDisplay = getCardsOrder(cards)

        orderDisplay shouldEqual "1       2"
      }
    }

    "getCardsDisplay" should {
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
    }

    "getOrderedCardsDisplay" should {
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
    }

    "getToDefendDisplay" should {
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
    }

    "getDefendedDisplay" should {
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
    }

    "getOwnDisplay" should {
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
    }

    "clearScreen" should {
      "clear screen by printing 100 lines" in {
        val outContent = ByteArrayOutputStream()
        Console.withOut(PrintStream(outContent)) {
          clearScreen()
        }

        val output = outContent.toString.trim
        output shouldEqual ("\n" * 100).trim
      }
    }

    "askForPickUp" should {
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

      "continue prompting for valid input" in {
        val input = "X\nY\nJ\n"
        val inputStream = new ByteArrayInputStream(input.getBytes)
        val outputStream = new ByteArrayOutputStream()

        Console.withIn(inputStream) {
          Console.withOut(new PrintStream(outputStream)) {
            val result = askForPickUp()

            result shouldBe true
          }
        }
      }
    }


    "askForCard" should {
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

      "ask for a card and return None when the cards list is empty" in {
        val emptyCards: List[Card] = List()

        val result = askForCard("Select a card", emptyCards, pickUp = false)

        result should be(None)
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

      "continue prompting for valid input" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Ten, Suit.Diamonds))

        val input = "0\n4\n2\n"
        val inputStream = new ByteArrayInputStream(input.getBytes)
        val outputStream = new ByteArrayOutputStream()

        Console.withIn(inputStream) {
          Console.withOut(new PrintStream(outputStream)) {
            val result = askForCard("Select a card", cards, pickUp = false)

            result shouldBe Some(cards(1))
          }
        }
      }
    }

    "askForDefend" should {
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
    }


    "askForOwn" should {
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
