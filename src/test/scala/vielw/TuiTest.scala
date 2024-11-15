import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.Controller
import model._
import observer.Observer
import view.Tui

class TuiSpec extends AnyWordSpec with Matchers {

  "A Tui" should {

    "update correctly when the turn is FirstlyAttacking" in {
      val controller = new Controller {
        var updateCalled = false

        val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
        val trump = Card(Rank.Ten, Suit.Spades)

        override def status: Status = Status(Group(List(Player("Player1", List.empty, Turn.FirstlyAttacking)),stack, trump, 6),
          Round(Turn.FirstlyAttacking, List.empty, List.empty, List.empty, None, false))
        override def byTurn(turn: Turn): Option[Player] = Some(Player("Player1", List.empty, Turn.FirstlyAttacking))

        override def notifyObservers(): Unit = {
          updateCalled = true
        }
        // Implement other methods as needed
      }

      val tui = new Tui(controller)
      tui.update()

      controller.updateCalled shouldBe true
    }

    "update correctly when the turn is Defending" in {
      val controller = new Controller {
        var updateCalled = false

        val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
        val trump = Card(Rank.Ten, Suit.Spades)

        override def status: Status = Status(Group(List(Player("Player1", List.empty, Turn.Defending)), stack, trump,6),
          Round(Turn.Defending, List.empty, List.empty, List.empty, None, false))
        override def byTurn(turn: Turn): Option[Player] = Some(Player("Player1", List.empty, Turn.Defending))

        override def notifyObservers(): Unit = {
          updateCalled = true
        }
        // Implement other methods as needed
      }

      val tui = new Tui(controller)
      tui.update()

      controller.updateCalled shouldBe true
    }

    "start correctly by asking for the defending player" in {
      val controller = new Controller {
        var defendingPlayerAsked = false

        val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
        val trump = Card(Rank.Ten, Suit.Spades)
        
        override def status: Status = Status(Group(List.empty, stack, trump, 6),
          Round(Turn.FirstlyAttacking, List.empty, List.empty, List.empty, None, false))

        override def setDefendingPlayer(name: String): Unit = {
          if (name == "Player1") defendingPlayerAsked = true
        }
        // Implement other methods as needed
      }

      val tui = new Tui(controller)

      // Simulate user input
      val input = new java.io.ByteArrayInputStream("Player1\n".getBytes)
      Console.withIn(input) {
        tui.start()
      }

      controller.defendingPlayerAsked shouldBe true
    }

    "ask for a defending player correctly" in {
      val controller = new Controller {
        var defendingPlayerAsked = false

        override def setDefendingPlayer(name: String): Unit = {
          if (name == "Player1") defendingPlayerAsked = true
        }
        // Implement other methods as needed
      }

      val tui = new Tui(controller)

      // Simulate user input
      val input = new java.io.ByteArrayInputStream("Player1\n".getBytes)
      Console.withIn(input) {
        tui.askForDefendingPlayer()
      }

      controller.defendingPlayerAsked shouldBe true
    }

    "display cards correctly" in {
      val controller = new Controller {
        // Implement methods as needed
      }
      val card = Card(Rank.Ace, Suit.Spades)
      val tui = new Tui(controller)

      val display = tui.getCardDisplay(card)
      display should not be empty
      display should include("A") // Assuming the display includes "A" for Ace
      display should include("♠") // Assuming the display includes "♠" for Spades
    }

    "handle attack correctly" in {
      val controller = new Controller {
        var attackMade = false

        val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
        val trump = Card(Rank.Ten, Suit.Spades)

        override def status: Status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.FirstlyAttacking)), stack, trump, 6),
          Round(Turn.FirstlyAttacking, List.empty, List.empty, List.empty, None, false))
        override def byTurn(turn: Turn): Option[Player] = Some(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.FirstlyAttacking))
        override def canAttack(card: Card): Boolean = true

        override def makeAttack(player: Player, card: Card): Boolean = {
          if (player.name == "Player1" && card == Card(Rank.Ace, Suit.Spades)) {
            attackMade = true
            true
          } else {
            false
          }
        }
        // Implement other methods as needed
      }

      val tui = new Tui(controller)

      // Simulate user input
      val input = new java.io.ByteArrayInputStream("1\n".getBytes)
      Console.withIn(input) {
        tui.askForAttack()
      }

      controller.attackMade shouldBe true
    }

    "handle defense correctly" in {
      val controller = new Controller {
        var defenseMade = false

        val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
        val trump = Card(Rank.Ten, Suit.Spades)

        override def status: Status = Status(Group(List(Player("Player1", List(Card(Rank.King, Suit.Spades)), Turn.Defending)), stack, trump, 6),
          Round(Turn.Defending, List(Card(Rank.Ace, Suit.Spades)), List.empty, List.empty, None, false))
        override def byTurn(turn: Turn): Option[Player] = Some(Player("Player1", List(Card(Rank.King, Suit.Spades)), Turn.Defending))
        override def canDefend(used: Card, undefended: Card): Boolean = true

        override def makeDefense(player: Player, used: Card, undefended: Card): Boolean = {
          if (player.name == "Player1" && used == Card(Rank.King, Suit.Spades) && undefended == Card(Rank.Ace, Suit.Spades)) {
            defenseMade = true
            true
          } else {
            false
          }
        }
        // Implement other methods as needed
      }

      val tui = new Tui(controller)

      // Simulate user input
      val input = new java.io.ByteArrayInputStream("1\n1\n".getBytes)
      Console.withIn(input) {
        tui.askForDefend()
      }

      controller.defenseMade shouldBe true
    }
  }
}