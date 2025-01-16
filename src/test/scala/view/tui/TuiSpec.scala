package view.tui

import controller.Controller
import model.status.Status
import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import view.tui.runner.Runner

class TuiSpec extends AnyWordSpec with Matchers {
  class MockRunner(var inputs: List[String]) extends Runner {
    override def run(run: () => Unit): Unit = run.apply()

    override def readLine(prompt: String): String = {
      val input = inputs.head

      inputs = inputs.tail

      input
    }
  }

  class MockController(var status: Status) extends Controller {

    override def initialize(amount: Int, names: List[String]): Unit = {}

    override def initialize(amount: Int, names: List[String], attacking: String): Unit = {}

    override def deny(): Unit = {}

    override def pickUp(): Unit = {}

    override def attack(card: Card): Unit = {}

    override def canAttack(card: Card): Boolean = true

    override def defend(used: Card, undefended: Card): Unit = {}

    override def canDefend(used: Card, undefended: Card): Boolean = true

    override def byTurn(turn: Turn): Option[Player] = None

    override def current: Option[Player] = None

    override def undo(): Unit = {}

    override def redo(): Unit = {}

    override def load(): Unit = {}

    override def save(): Unit = {}

    override def isOver: Boolean = false

    override def unbind(): Unit = {}
  }

  "Tui" should {
    "askForStep" should {
      "return Step.Continue when the user inputs 'f'" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("f")))

        tui.askForStep should be(Step.Continue)
      }

      "return Step.Undo when the user inputs 'r'" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("r")))

        tui.askForStep should be(Step.Undo)
      }

      "return Step.Redo when the user inputs 'w'" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("w")))

        tui.askForStep should be(Step.Redo)
      }

      "return Step.Load when the user inputs 'l'" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("l")))

        tui.askForStep should be(Step.Load)
      }

      "return Step.Save when the user inputs 's'" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("s")))

        tui.askForStep should be(Step.Save)
      }
    }

    "askForPlayerAmount" should {
      "return the valid player amount entered by the user" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("3")))

        tui.askForPlayerAmount should be(3)
      }

      "repeat the prompt until a valid input is provided" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("0", "one", "2")))

        tui.askForPlayerAmount should be(2)
      }
    }

    "askForCardAmount(Int)" should {
      "return the valid card amount entered by the user" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("6")))

        tui.askForCardAmount(4) should be(6)
      }

      "repeat the prompt until a valid input within the range is provided" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("100", "6")))

        tui.askForCardAmount(4) should be(6)
      }
    }

    "askForNames(Int)" should {
      "return a list of unique names for the players" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("Alice", "Bob", "Charlie")))

        tui.askForNames(3) should be(List("Alice", "Bob", "Charlie"))
      }

      "repeat the prompt until a unique name is provided" in {
        val tui = new Tui(new MockController(Status()), new MockRunner(List("Alice", "Alice", "Bob")))

        tui.askForNames(2) should be(List("Alice", "Bob"))
      }
    }

    "askForCard(String, List[Card], Boolean)" should {
      "return the selected card when a valid input is provided" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = new Tui(new MockController(Status()), new MockRunner(List("1")))

        tui.askForCard("", cards, cancel = false) should be(Some(cards.head))
      }

      "return None when the user chooses to cancel" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = new Tui(new MockController(Status()), new MockRunner(List("a")))

        tui.askForCard("", cards, cancel = true) should be(None)
      }

      "repeat the prompt until a valid input is provided" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = new Tui(new MockController(Status()), new MockRunner(List("3", "0", "2")))

        tui.askForCard("", cards, cancel = false) should be(Some(cards(1)))
      }
    }

  }
}