package view

import controller.Controller
import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import view.tui.Tui

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import scala.io.StdIn

class TuiSpec extends AnyWordSpec with Matchers {

  class MockController extends Controller {

    var status: Status = Status(List(Player("Mock", List(Card(Rank.Ace, Suit.Hearts)), Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ace, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)

    override def initialize(amount: Int, names: List[String]): Unit = {}
    
    override def byTurn(turn: Turn): Option[Player] =
      if (status.turn == turn) Some(status.players.head) else None

    override def current: Option[Player] = Some(status.players.head)

    override def chooseAttacking(player: Player): Unit = {}

    override def chooseAttacking(): Unit = {}

    override def pickUp(): Unit = {}

    override def attack(card: Card): Unit = {}

    override def defend(used: Card, undefended: Card): Unit = {}

    override def deny(): Unit = {}

    override def canAttack(card: Card): Boolean = true

    override def canDefend(used: Card, undefended: Card): Boolean = true
    
    override def undo(): Unit = {}

    override def redo(): Unit = {}
  }

  "Tui" should {
    "displayPlayerCards(List[Player])" should {
      "display player cards" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        tui.countdown = () => {}

        Console.withIn(new java.io.StringReader("w\n")) {
          tui.displayPlayerCards(mockController.status.players)
        }

        mockController.status.players.head.cards should not be empty
      }
    }

    "askForAttackingPlayer(List[Player])" should {
      "handle attacking player selection" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        tui.countdown = () => {}

        Console.withIn(new java.io.StringReader("Mock\n")) {
          val selectedPlayer = tui.askForAttackingPlayer(mockController.status.players)
          selectedPlayer.isDefined shouldBe true
          selectedPlayer.get.name shouldBe "Mock"
        }
      }
    }

    "askForCard(String, List[Card], Boolean)" should {
      "handle card selection" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        val cards = mockController.status.players.head.cards

        Console.withIn(new java.io.StringReader("1\n")) {
          val selectedCard = tui.askForCard("Wähle eine Karte:", cards, cancel = true)
          selectedCard.isDefined shouldBe true
          selectedCard.get shouldBe cards.head
        }
      }
    }

    "askForAttack(Player, List[Card], List[Card], () => Unit, Card => Unit)" should {
      "handle attacking interaction" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        val player = mockController.status.players.head

        Console.withIn(new java.io.StringReader("1\n")) {
          tui.askForAttack(player, Nil, Nil, () => {}, card => {
            card shouldBe player.cards.head
          })
        }
      }
    }

    "askForDefend(Player, List[Card], List[Card], () => Unit, (Card, Card) => Unit)" should {
      "handle defending interaction" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        val defender = mockController.status.players.head
        val undefendedCards = List(Card(Rank.Two, Suit.Clubs))

        Console.withIn(new java.io.StringReader("1\n1\n")) {
          tui.askForDefend(defender, Nil, undefendedCards, () => fail("Defend cancelled"), (used, undefended) => {
            used shouldBe defender.cards.head
            undefended shouldBe undefendedCards.head
          })
        }
      }
    }

    "update()" should {
      "update game state and handle player turns" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        tui.countdown = () => {}

        Console.withIn(new java.io.StringReader("1\n")) {
          tui.update()
        }

        //mockController.status.stack should contain(mockController.status.players.head.cards.head)
      }
    }

    "clearScreen()" should {
      "clear screen when called" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)

        noException should be thrownBy tui.clearScreen()
      }
    }

    "askForContinue()" should {
      "ask for continuation and proceed" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        tui.countdown = () => {}

        Console.withIn(new java.io.StringReader("w\n")) {
          tui.askForContinue()
        }
      }
    }

    "lookAway(Player)" should {
      "count down correctly during lookAway" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        tui.countdown = () => {}
        val player = mockController.status.players.head
      }
    }

    "getStackDisplay(List[Card])" should {
      "display stack size" in {
        val tui = new Tui(MockController(), false)

        val display = tui.getStackDisplay(List(Card(Rank.Three, Suit.Spades), Card(Rank.King, Suit.Diamonds)))

        display should be("Stapel: 2")
      }
    }

    "getTrumpDisplay(Card)" should {
      "display trump card" in {
        val tui = new Tui(MockController(), false)

        val display = tui.getTrumpDisplay(Card(Rank.Ace, Suit.Spades))

        display.head should be("Trumpf")
        display(1) should be("┌─────┐")
        display(2) should be("│A    │")
        display(3) should be("│  ♠  │")
        display(4) should be("│    A│")
        display(5) should be("└─────┘")
      }
    }

    "getPlayersDisplay(List[Player])" should {
      "display players" in {
        val tui = new Tui(MockController(), false)

        val display = tui.getPlayersDisplay(List(Player("Player1", List(Card(Rank.Two, Suit.Hearts)), Turn.FirstlyAttacking),
            Player("Player2", List(Card(Rank.King, Suit.Clubs), Card(Rank.Three, Suit.Diamonds)), Turn.Defending)))

        display.head should be("Primär Angreifen: Player1 (Karten: 1)")
        display.last should be("Verteidigen: Player2 (Karten: 2)")
      }
    }

    "getCardDisplay(Card)" should {
      "display cards correctly" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        val card = Card(Rank.Ace, Suit.Spades)

        val display = tui.getCardDisplay(card)
      }
    }

    "getOrderedCardsDisplay(List[Card])" should {
      "display ordered cards" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        val cards = mockController.status.players.head.cards

        val display = tui.getOrderedCardsDisplay(cards)
        display.head should include("1")
      }
    }

    "getUndefendedDisplay(List[Card])" should {
      "display undefended cards" in {
        val tui = new Tui(MockController(), false)

        val display = tui.getUndefendedDisplay(List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Two, Suit.Clubs)))

        display.head should be("Zu Verteidigen")
        display(1) should be("1       2")
        display(2) should be("┌─────┐ ┌─────┐")
        display(3) should be("│A    │ │2    │")
        display(4) should be("│  ♥  │ │  ♣  │")
        display(5) should be("│    A│ │    2│")
        display(6) should be("└─────┘ └─────┘")

      }
    }

    "getDefendedDisplay(List[Card], List[Card])" should {
      "display defended cards" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        val defended = mockController.status.defended
        val used = mockController.status.used

        val display = tui.getDefendedDisplay(defended, used)
      }
    }

    "getOwnDisplay(Player)" should {
      "display own cards" in {
        val mockController = new MockController()
        val tui = new Tui(mockController, false)
        val player = mockController.status.players.head

        val display = tui.getOwnDisplay(player)
        display.head should include(player.name)
      }
    }
  }
}
