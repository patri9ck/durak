package view.tui

import controller.Controller
import model.{Card, Player, Rank, Status, Suit, Turn}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import scala.io.StdIn

class TuiSpec extends AnyWordSpec with Matchers {

  class MockController extends Controller {

    var status: Status = Status(List(Player("Mock", List(Card(Rank.Ace, Suit.Hearts)), Turn.FirstlyAttacking)), Nil, Card(Rank.Ace, Suit.Spades), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)

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

    "initialize correctly with start" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      tui.countdown = () => {}

      Console.withIn(new java.io.StringReader("\nw\nw\nz")) {
        tui.start()
      }

      mockController.status.players.head.name shouldBe "Mock"
      mockController.status.players.length shouldBe 1
    }

    "display player cards with displayPlayerCards" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      tui.countdown = () => {}

      Console.withIn(new java.io.StringReader("w\n")) {
        tui.displayPlayerCards(mockController.status.players)
      }

      mockController.status.players.head.cards should not be empty
    }

    "handle attacking player selection with askForAttackingPlayer" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      tui.countdown = () => {}

      Console.withIn(new java.io.StringReader("Mock\n")) {
        val selectedPlayer = tui.askForAttackingPlayer(mockController.status.players)
        selectedPlayer.isDefined shouldBe true
        selectedPlayer.get.name shouldBe "Mock"
      }
    }

    "handle card selection with askForCard" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      val cards = mockController.status.players.head.cards

      Console.withIn(new java.io.StringReader("1\n")) {
        val selectedCard = tui.askForCard("Wähle eine Karte:", cards, cancel = true)
        selectedCard.isDefined shouldBe true
        selectedCard.get shouldBe cards.head
      }
    }

    "handle attacking interaction with askForAttack" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      val player = mockController.status.players.head

      Console.withIn(new java.io.StringReader("1\n")) {
        tui.askForAttack(player, Nil, Nil, () => {}, card => {
          card shouldBe player.cards.head
        })
      }
    }

    "handle defending interaction with askForDefend" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      val defender = mockController.status.players.head
      val undefendedCards = List(Card(Rank.Two, Suit.Clubs))

      Console.withIn(new java.io.StringReader("1\n1\n")) {
        tui.askForDefend(defender, Nil, undefendedCards, () => fail("Defend cancelled"), (used, undefended) => {
          used shouldBe defender.cards.head
          undefended shouldBe undefendedCards.head
        })
      }
    }

    "update game state and handle player turns" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      tui.countdown = () => {}

      Console.withIn(new java.io.StringReader("1\n")) {
        tui.update()
      }

      //mockController.status.stack should contain(mockController.status.players.head.cards.head)
    }

    "clear screen when called" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)

      noException should be thrownBy tui.clearScreen()
    }

    "ask for continuation and proceed" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      tui.countdown = () => {}

      Console.withIn(new java.io.StringReader("w\n")) {
        tui.askForContinue()
      }
    }

    "count down correctly during lookAway" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      tui.countdown = () => {}
      val player = mockController.status.players.head
    }

    "display stack size with getStackDisplay" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      tui.countdown = () => {}

      tui.getStackDisplay(mockController.status.stack) shouldBe "Stapel: 0"
    }

    "display trump card with getTrumpDisplay" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      val trumpCard = mockController.status.trump

      tui.getTrumpDisplay(trumpCard) should contain(s"Trumpf")
      //tui.getTrumpDisplay(trumpCard).mkString should include(trumpCard.toString)
    }

    "display players with getPlayersDisplay" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)

      val display = tui.getPlayersDisplay(mockController.status.players)
      display.head should include(mockController.status.players.head.name)
    }

    "display cards correctly with getCardDisplay" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      val card = Card(Rank.Ace, Suit.Spades)

      val display = tui.getCardDisplay(card)
    }

    "display ordered cards with getOrderedCardsDisplay" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      val cards = mockController.status.players.head.cards

      val display = tui.getOrderedCardsDisplay(cards)
      display.head should include("1")
    }

    "display undefended cards with getUndefendedDisplay" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      mockController.status = mockController.status.copy(undefended = List(Card(Rank.Two, Suit.Hearts)))

      val display = tui.getUndefendedDisplay(mockController.status.undefended)
      display should contain("Zu Verteidigen")
    }

    "display defended cards with getDefendedDisplay" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      mockController.status = mockController.status.copy(defended = List(Card(Rank.Three, Suit.Diamonds)))

      val display = tui.getDefendedDisplay(mockController.status.defended, mockController.status.used)
      display should contain("Verteidigt")
    }

    "display own cards with getOwnDisplay" in {
      val mockController = new MockController()
      val tui = new Tui(mockController)
      val player = mockController.status.players.head

      val display = tui.getOwnDisplay(player)
      display.head should include(player.name)
    }
  }

}
