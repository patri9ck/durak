package view

import controller.Controller
import model.*
import observer.Observer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import view.tui.Tui

import scala.collection.immutable.List


class TuiSpec extends AnyWordSpec with Matchers {

  class MockController extends Controller {

    var status: Status = Status(List(Player("Mock", List(Card(Rank.Ace, Suit.Hearts)), Turn.FirstlyAttacking)), List.empty, Card(Rank.Ace, Suit.Spades), 6, Turn.FirstlyAttacking, List.empty, List.empty, List.empty, false, None)

    override def add(obs: Observer): Unit = {}

    override def remove(obs: Observer): Unit = {}

    override def byTurn(turn: Turn): Option[Player] =
      if (status.turn == turn) Some(status.players.head) else None

    override def getPlayer: Option[Player] = Some(status.players.head)

    def chooseAttacking(player: Player): Unit = {}

    def chooseAttacking(): Unit = {}

    def pickUp(): Unit = {}

    def attack(card: Card): Unit = {}

    def defend(used: Card, undefended: Card): Unit = {}

    def denied(): Unit = {}

    def canAttack(card: Card): Boolean = true

    def canDefend(used: Card, undefended: Card): Boolean = true
  }

  "Tui" should {
    "return None when 'z' is input for askForAttackingPlayer" in {
      val players = List(
        Player("Player1", List(), Turn.Watching),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )

      val mockController = new MockController
      val tui = new Tui(mockController)

      val in = new java.io.ByteArrayInputStream("z\n".getBytes)
      Console.withIn(in) {
        tui.askForAttackingPlayer(players) should be(None)
      }
    }

    "return a correctly formatted card display string for getCardDisplay" in {
      val mockController = new MockController
      val tui = new Tui(mockController)
      val card = Card(Rank.Ace, Suit.Hearts)

      val display = tui.getCardDisplay(card)
      display should contain("┌─────┐")
      display should contain("│A    │")
      display should contain("│  ♥  │")
      display should contain("│    A│")
      display should contain("└─────┘")
    }

    "return a correctly formatted cards order string for getCardsOrder" in {
      val mockController = new MockController
      val tui = new Tui(mockController)
      val cards = List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Two, Suit.Spades))

      val order = tui.getCardsOrder(cards)
      order should be ("1       2")
    }

    "return an empty list for getOrderedCardsDisplay when cards list is empty" in {
      val mockController = new MockController
      val tui = new Tui(mockController)

      val display = tui.getOrderedCardsDisplay(Nil)
      display should be (List.empty)
    }

    "return a list of formatted card displays for getOrderedCardsDisplay when cards list is not empty" in {
      val mockController = new MockController
      val tui = new Tui(mockController)
      val cards = List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Two, Suit.Spades))

      val display = tui.getOrderedCardsDisplay(cards)
      display.head should be ("1       2")
      display should contain ("┌─────┐ ┌─────┐")
    }

    /*"print the correct displays when update is called" in {
      val mockController = new MockController
      val tui = new Tui(mockController) {
        override def askForAttack(): Unit = {}  // Leere Implementation für den Test
        override def askForDefend(): Unit = {}  // Leere Implementation für den Test
      }

      def captureConsoleOutput(action: => Unit): String = {
        val outCapture = new java.io.ByteArrayOutputStream
        Console.withOut(outCapture) {
          action
        }
        outCapture.toString
      }

      val result = captureConsoleOutput {
        tui.update()
      }

      val expectedOutputs = Seq(
        "Mock, Deine Karten",
        "1",
        "┌─────┐",
        "│A    │",
        "│  ♥  │",
        "│    A│",
        "└─────┘"
      )

      expectedOutputs.foreach(output => result should include(output))
    }*/

    "clear the screen when clearScreen is called" in {
      val EXPECTED_OUTPUT = "\n" * 100
      val mockController = new MockController
      val tui = new Tui(mockController)
      val outputCapture = new java.io.ByteArrayOutputStream
      Console.withOut(outputCapture) {
        tui.clearScreen()
      }
      val output = outputCapture.toString.trim
      output should be (EXPECTED_OUTPUT.trim)
    }

    "return the correct card when askForCard is called" in {
      val mockController = new MockController
      val tui = new Tui(mockController)
      val cards = List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Two, Suit.Spades))

      val in = new java.io.ByteArrayInputStream("1\n".getBytes)
      val outCapture = new java.io.ByteArrayOutputStream
      Console.withIn(in) {
        Console.withOut(outCapture) {
          val result = tui.askForCard("Choose a card", cards, cancel = false)
          result should be (Some(cards.head))
        }
      }
    }

    "return the correct defending card when askForCard is called with cancel option" in {
      val mockController = new MockController
      val tui = new Tui(mockController)
      val cards = List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Two, Suit.Spades))

      val in = new java.io.ByteArrayInputStream("a\n".getBytes)
      val outCapture = new java.io.ByteArrayOutputStream
      Console.withIn(in) {
        Console.withOut(outCapture) {
          val result = tui.askForCard("Choose a card", cards, cancel = true)
          result should be(None)
        }
      }
    }

    /*"call attack on controller when a valid card is chosen in askForAttack" in {
      val mockController = new MockController
      val tui = new Tui(mockController)

      val in = new java.io.ByteArrayInputStream("1\n".getBytes)
      Console.withIn(in) {
        tui.askForAttack()
      }

    }

    "call defend on controller when valid cards are chosen in askForDefend" in {
      val mockController = new MockController
      val tui = new Tui(mockController)

      val in = new java.io.ByteArrayInputStream("1\n1\n".getBytes)
      Console.withIn(in) {
        tui.askForDefend()
      }
    }

    "call pickUp controller when 'a' is input for askForCard in askForDefend" in {
      val mockController = new MockController
      val tui = new Tui(mockController)

      val in = new java.io.ByteArrayInputStream("a\n".getBytes)
      Console.withIn(in) {
        tui.askForDefend()
      }
    }*/

   /* "return status correctly when createStatus is called in Tui object" in {
      val in = new java.io.ByteArrayInputStream("2\n3\nplayer1\nplayer2\n".getBytes)
      Console.withIn(in) {
        val status = Tui.createStatus()
        status.group.players.size should be (2)
      }
    }*/

    "return correct number of cards when askForCardAmount is called in Tui object" in {
      val in = new java.io.ByteArrayInputStream("3\n".getBytes)
      Console.withIn(in) {
        val cardAmount = Tui.askForCardAmount(4)
        cardAmount should be (3)
      }
    }

    "return correct number of players when askForPlayerAmount is called in Tui object" in {
      val in = new java.io.ByteArrayInputStream("3\n".getBytes)
      Console.withIn(in) {
        val playerAmount = Tui.askForPlayerAmount
        playerAmount should be (3)
      }
    }

    "return correct player names when askForNames is called in Tui object" in {
      val in = new java.io.ByteArrayInputStream("player1\nplayer2\n".getBytes)
      Console.withIn(in) {
        val playerNames = Tui.askForNames(2)
        playerNames should contain("player1")
        playerNames should contain("player2")
      }
    }
  }
}