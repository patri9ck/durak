package view.tui

import controller.Controller
import model.*
import model.status.Status
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import view.tui.runner.Runner

import java.io.ByteArrayOutputStream

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
    var canAttack = true
    var canDefend = true

    var initialized = false
    var denied = false
    var pickedUp = false
    var attacked = false
    var defended = false
    var undid = false
    var redid = false
    var loaded = false
    var saved = false

    override def initialize(amount: Int, names: List[String]): Unit = initialized = true

    override def initialize(amount: Int, names: List[String], attacking: String): Unit = initialized = true

    override def deny(): Unit = denied = true

    override def pickUp(): Unit = pickedUp = true

    override def attack(card: Card): Unit = attacked = true

    override def canAttack(card: Card): Boolean = canAttack

    override def defend(used: Card, undefended: Card): Unit = defended = true

    override def canDefend(used: Card, undefended: Card): Boolean = canDefend

    override def byTurn(turn: Turn): Option[Player] = status.players.find(_.turn == turn)

    override def current: Option[Player] = status.players.find(_.turn == status.turn)

    override def undo(): Unit = undid = true

    override def redo(): Unit = redid = true

    override def load(): Unit = loaded = true

    override def save(): Unit = saved = true

    override def isOver: Boolean = false

    override def unbind(): Unit = {}
  }

  "Tui" should {
    "update()" should {
      "continue for example" in {
        val controller = MockController(Status(turn = Turn.Uninitialized))
        val tui = Tui(controller, MockRunner(List("2", "6", "Player1", "Player2", "Player1")))

        tui.update()

        controller.initialized should be(true)
      }
    }

    "start()" should {
      "ask whether the game should be controllable and continue" in {
        val controller = MockController(Status(turn = Turn.Uninitialized))
        val tui = Tui(controller, MockRunner(List("j", "f", "2", "6", "Player1", "Player2", "Player1")))

        tui.start()

        controller.initialized should be(true)
      }
    }

    "run()" should {
      "continue if the game is not controllable" in {
        val controller = MockController(Status(turn = Turn.Uninitialized))
        val tui = Tui(controller, MockRunner(List("2", "6", "Player1", "Player2", "Player1")))

        tui.run()

        controller.initialized should be(true)
      }

      "continue if the game is controllable and the user enters 'f'" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(List("f", "2", "6", "Player1", "Player2", "Player1")))

        tui.controllable = true
        tui.run()

        controller.initialized should be(true)
      }

      "undo if the game is controllable and the user enters 'r'" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(List("r")))

        tui.controllable = true
        tui.run()

        controller.undid should be(true)
      }

      "redo if the game is controllable and the user enters 'w'" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(List("w")))

        tui.controllable = true
        tui.run()

        controller.redid should be(true)
      }

      "load if the game is controllable and the user enters 'l'" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(List("l")))

        tui.controllable = true
        tui.run()

        controller.loaded should be(true)
      }

      "save if the game is controllable and the user enters 's'" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(List("s")))

        tui.controllable = true
        tui.run()

        controller.saved should be(true)
      }
    }

    "continue()" should {
      "initialize the game if the turn is Uninitialized with a specified player" in {
        val controller = MockController(Status(turn = Turn.Uninitialized))
        val tui = Tui(controller, MockRunner(List("2", "6", "Player1", "Player2", "Player1")))

        tui.continue()

        controller.initialized should be(true)
      }

      "initialize the game if the turn is Uninitialized with a random player" in {
        val controller = MockController(Status(turn = Turn.Uninitialized))
        val tui = Tui(controller, MockRunner(List("2", "6", "Player1", "Player2", "z")))

        tui.continue()

        controller.initialized should be(true)
      }

      "ask for an attack if the turn is FirstlyAttacking and attack" in {
        val controller = MockController(Status(turn = Turn.FirstlyAttacking, players = List(Player("Player1", List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), Turn.FirstlyAttacking)), trump = Some(Card(Rank.Ace, Suit.Hearts))))
        val tui = Tui(controller, MockRunner(List("1")), 0, 0)

        tui.continue()

        controller.attacked should be(true)
      }

      "ask for an attack if the turn is FirstlyAttacking and deny" in {
        val controller = MockController(Status(turn = Turn.FirstlyAttacking, players = List(Player("Player1", List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), Turn.FirstlyAttacking)), trump = Some(Card(Rank.Ace, Suit.Hearts)), defended = List(Card(Rank.Ace, Suit.Hearts))))
        val tui = Tui(controller, MockRunner(List("a")), 0, 0)

        tui.continue()

        controller.denied should be(true)
      }

      "ask for an attack if the turn is SecondlyAttacking " in {
        val controller = MockController(Status(turn = Turn.SecondlyAttacking, players = List(Player("Player1", List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), Turn.SecondlyAttacking)), trump = Some(Card(Rank.Ace, Suit.Hearts))))
        val tui = Tui(controller, MockRunner(List("1")), 0, 0)

        tui.continue()

        controller.attacked should be(true)
      }

      "ask for a defend if the turn is Defending and defend" in {
        val controller = MockController(Status(turn = Turn.Defending, players = List(Player("Player1", List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), Turn.Defending)), trump = Some(Card(Rank.Ace, Suit.Hearts)), undefended = List(Card(Rank.Ace, Suit.Hearts))))
        val tui = Tui(controller, MockRunner(List("1", "1")), 0, 0)

        tui.continue()

        controller.defended should be(true)
      }

      "ask for a defend if the turn is Defending and pick up" in {
        val controller = MockController(Status(turn = Turn.Defending, players = List(Player("Player1", List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), Turn.Defending)), trump = Some(Card(Rank.Ace, Suit.Hearts)), undefended = List(Card(Rank.Ace, Suit.Hearts))))
        val tui = Tui(controller, MockRunner(List("a")), 0, 0)

        tui.continue()

        controller.pickedUp should be(true)
      }

      "not ask for anything if the current player is not FirstlyAttacking, SeconldyAttacking or Defending" in {
        val controller = MockController(Status(turn = Turn.Finished, players = List(Player("Player1", List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), Turn.Finished)), trump = Some(Card(Rank.Ace, Suit.Hearts))))
        val runner = MockRunner(List("1"))
        val tui = Tui(controller, runner, 0, 0)

        tui.continue()

        runner.inputs should be(List("1"))
      }
    }

    "deny()" should {
      "deny" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(Nil))

        tui.deny()

        controller.denied should be(true)
      }
    }

    "attack(Card)" should {
      "return true on success and attack" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(Nil))

        tui.attack(Card(Rank.Ace, Suit.Hearts)) should be(true)
        controller.attacked should be(true)
      }

      "return false on failure" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(Nil))

        controller.canAttack = false

        tui.attack(Card(Rank.Ace, Suit.Hearts)) should be(false)
        controller.attacked should be(false)
      }
    }

    "pickUp()" should {
      "pick up" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(Nil))

        tui.pickUp()

        controller.pickedUp should be(true)
      }
    }

    "defend()" should {
      "return true on success and defend" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(Nil))

        tui.defend(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)) should be(true)
        controller.defended should be(true)
      }

      "return false on failure" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(Nil))

        controller.canDefend = false

        tui.defend(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)) should be(false)
        controller.defended should be(false)
      }
    }

    "countdown()" should {
      "count down the specified seconds" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(Nil), 1, 0)

        val out = ByteArrayOutputStream()

        Console.withOut(out) {
          tui.countdown()
        }

        out.toString should be("1...\n")
      }
    }

    "lookAway(Player)" should {
      "should alert all other players to look away, countdown and clear the display" in {
        val controller = MockController(Status())
        val tui = Tui(controller, MockRunner(Nil), 1, 1)

        val out = ByteArrayOutputStream()

        Console.withOut(out) {
          tui.lookAway(Player("Player1", Nil, Turn.FirstlyAttacking))
        }

        out.toString should be("Player1, Du bist dran. Alle anderen wegschauen!\n1...\n\n\n")
      }
    }

    "getClearDisplay" should {
      "return the correct number of newlines" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil), 0, 5)

        tui.getClearDisplay should be("\n" * 5)
      }
    }

    "getStackDisplay(List[Card])" should {
      "should display the stack size" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil), 3, 5)

        tui.getStackDisplay(List(Card(Rank.Ace, Suit.Hearts), Card(Rank.King, Suit.Spades))) should be("Stapel: 2")
      }
    }

    "getTrumpDisplay(Card)" should {
      "should display the trump" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil), 3, 5)

        tui.getTrumpDisplay(Card(Rank.Ace, Suit.Hearts)) should be(List(
          "Trumpf",
          "┌─────┐",
          "│A    │",
          "│  ♥  │",
          "│    A│",
          "└─────┘"))
      }
    }

    "getLookAwayDisplay(Player)" should {
      "should alert all other players to look away and print the player's name" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getLookAwayDisplay(Player("Player1", Nil, Turn.FirstlyAttacking)) should be("Player1, Du bist dran. Alle anderen wegschauen!")
      }
    }

    "getCountdownDisplay(Int)" should {
      "should return a list of countdown numbers" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getCountdownDisplay(3) should be(List("3...", "2...", "1..."))
      }
    }

    "askForStep" should {
      "return Step.Continue when the user inputs 'f'" in {
        val tui = Tui(MockController(Status()), MockRunner(List("f")))

        tui.askForStep should be(Step.Continue)
      }

      "return Step.Undo when the user inputs 'r'" in {
        val tui = Tui(MockController(Status()), MockRunner(List("r")))

        tui.askForStep should be(Step.Undo)
      }

      "return Step.Redo when the user inputs 'w'" in {
        val tui = Tui(MockController(Status()), MockRunner(List("w")))

        tui.askForStep should be(Step.Redo)
      }

      "return Step.Load when the user inputs 'l'" in {
        val tui = Tui(MockController(Status()), MockRunner(List("l")))

        tui.askForStep should be(Step.Load)
      }

      "return Step.Save when the user inputs 's'" in {
        val tui = Tui(MockController(Status()), MockRunner(List("s")))

        tui.askForStep should be(Step.Save)
      }

      "repeat until a valid user input is provided" in {
        val tui = Tui(MockController(Status()), MockRunner(List("x", "y", "s")))

        tui.askForStep should be(Step.Save)
      }
    }

    "getCardDisplay(Card)" should {
      "display a card correctly" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil), 3, 5)

        tui.getCardDisplay(Card(Rank.Ace, Suit.Hearts)) should be(List(
          "┌─────┐",
          "│A    │",
          "│  ♥  │",
          "│    A│",
          "└─────┘"))
      }
    }

    "getRoundDisplay(List[Card], List[Card], List[Card])" should {
      "display undefended, defended and used cards" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getRoundCardsDisplay(List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), List(Card(Rank.Two, Suit.Diamonds), Card(Rank.Eight, Suit.Clubs)), List(Card(Rank.Four, Suit.Hearts), Card(Rank.Seven, Suit.Spades))) should be(List(
          "Zu Verteidigen",
          "1       2",
          "┌─────┐ ┌─────┐",
          "│A    │ │10   │",
          "│  ♥  │ │  ♠  │",
          "│    A│ │   10│",
          "└─────┘ └─────┘",
          "Verteidigt",
          "┌─────┐ ┌─────┐",
          "│2    │ │8    │",
          "│  ♦  │ │  ♣  │",
          "│    2│ │    8│",
          "└─────┘ └─────┘",
          "┌─────┐ ┌─────┐",
          "│4    │ │7    │",
          "│  ♥  │ │  ♠  │",
          "│    4│ │    7│",
          "└─────┘ └─────┘"))
      }
    }

    "getCardsOrder(List[Card])" should {
      "display an order for cards" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getCardDisplay(Card(Rank.Ace, Suit.Hearts)) should be(List(
          "┌─────┐",
          "│A    │",
          "│  ♥  │",
          "│    A│",
          "└─────┘"))
      }
    }

    "getCardsDisplay(List[Card])" should {
      "display multiple cards next to each other" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getCardsDisplay(List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades))) should be(List(
          "┌─────┐ ┌─────┐",
          "│A    │ │10   │",
          "│  ♥  │ │  ♠  │",
          "│    A│ │   10│",
          "└─────┘ └─────┘"))
      }
    }

    "getOrderedCardDisplay(List[Card])" should {
      "return an empty list if an empty list is passed" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getOrderedCardsDisplay(Nil) should be(Nil)
      }

      "display cards ordered" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getOrderedCardsDisplay(List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades))) should be(List(
          "1       2",
          "┌─────┐ ┌─────┐",
          "│A    │ │10   │",
          "│  ♥  │ │  ♠  │",
          "│    A│ │   10│",
          "└─────┘ └─────┘"))
      }
    }

    "getUndefendedDisplay(List[Card])" should {
      "return an empty list if an empty list is passed" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getUndefendedDisplay(Nil) should be(Nil)
      }

      "display undefended cards with a header" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getUndefendedDisplay(List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades))) should be(List(
          "Zu Verteidigen",
          "1       2",
          "┌─────┐ ┌─────┐",
          "│A    │ │10   │",
          "│  ♥  │ │  ♠  │",
          "│    A│ │   10│",
          "└─────┘ └─────┘"))
      }
    }

    "getDefendedDisplay(List[Card], List[Card])" should {
      "return an empty list if an empty defended and used list is passed" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getDefendedDisplay(Nil, Nil) should be(Nil)
      }

      "display defended cards and used cards" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getDefendedDisplay(List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), List(Card(Rank.Two, Suit.Diamonds), Card(Rank.Eight, Suit.Clubs))) should be(List(
          "Verteidigt",
          "┌─────┐ ┌─────┐",
          "│A    │ │10   │",
          "│  ♥  │ │  ♠  │",
          "│    A│ │   10│",
          "└─────┘ └─────┘",
          "┌─────┐ ┌─────┐",
          "│2    │ │8    │",
          "│  ♦  │ │  ♣  │",
          "│    2│ │    8│",
          "└─────┘ └─────┘"))
      }
    }

    "getOwnDisplay(Player)" should {
      "should display a player's name with his cards" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getOwnDisplay(Player("Player1", List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), Turn.FirstlyAttacking)) should be(List(
          "Player1, Deine Karten",
          "1       2",
          "┌─────┐ ┌─────┐",
          "│A    │ │10   │",
          "│  ♥  │ │  ♠  │",
          "│    A│ │   10│",
          "└─────┘ └─────┘"))
      }
    }

    "askForControllable" should {
      "return true when the user inputs 'y'" in {
        val tui = Tui(MockController(Status()), MockRunner(List("j")))

        tui.askForControllable should be(true)
      }

      "return false when the user inputs 'n'" in {
        val tui = Tui(MockController(Status()), MockRunner(List("n")))

        tui.askForControllable should be(false)
      }

      "repeat until a valid user input is provided" in {
        val tui = Tui(MockController(Status()), MockRunner(List("x", "y", "j")))

        tui.askForControllable should be(true)
      }
    }

    "askForPlayerAmount" should {
      "return the valid player amount entered by the user" in {
        val tui = Tui(MockController(Status()), MockRunner(List("3")))

        tui.askForPlayerAmount should be(3)
      }

      "repeat the prompt until a valid input is provided" in {
        val tui = Tui(MockController(Status()), MockRunner(List("0", "one", "2")))

        tui.askForPlayerAmount should be(2)
      }
    }

    "getPlayersDisplay(List[Player])" should {
      "display player names with their turn and card amount" in {
        val tui = Tui(MockController(Status()), MockRunner(Nil))

        tui.getPlayersDisplay(List(Player("Player1", List(Card(Rank.Ace, Suit.Hearts), Card(Rank.Ten, Suit.Spades)), Turn.Defending), Player("Player2", List(Card(Rank.Two, Suit.Diamonds), Card(Rank.Eight, Suit.Clubs)), Turn.FirstlyAttacking))) should be(List(
          "Verteidigen: Player1 (Karten: 2)",
          "Primär Angreifen: Player2 (Karten: 2)"))
      }
    }

    "askForContinue()" should {
      "wait until the user inputs 'j'" in {
        val tui = Tui(MockController(Status()), MockRunner(List("x", "y", "j")))

        noException should be thrownBy tui.askForContinue()
      }
    }

    "askForCardAmount(Int)" should {
      "return the valid card amount entered by the user" in {
        val tui = Tui(MockController(Status()), MockRunner(List("6")))

        tui.askForCardAmount(4) should be(6)
      }

      "repeat the prompt until a valid input within the range is provided" in {
        val tui = Tui(MockController(Status()), MockRunner(List("100", "6")))

        tui.askForCardAmount(4) should be(6)
      }
    }

    "askForNames(Int)" should {
      "return a list of unique names for the players" in {
        val tui = Tui(MockController(Status()), MockRunner(List("Alice", "Bob", "Charlie")))

        tui.askForNames(3) should be(List("Alice", "Bob", "Charlie"))
      }

      "repeat the prompt until a unique name is provided" in {
        val tui = Tui(MockController(Status()), MockRunner(List("Alice", "Alice", "Bob")))

        tui.askForNames(2) should be(List("Alice", "Bob"))
      }
    }

    "askForAttacking(List[String])" should {
      "return the selected player when a valid input is provided" in {
        val tui = Tui(MockController(Status()), MockRunner(List("Alice")))

        tui.askForAttacking(List("Alice", "Bob", "Charlie")) should be(Some("Alice"))
      }

      "return None when the user chooses to cancel" in {
        val tui = Tui(MockController(Status()), MockRunner(List("z")))

        tui.askForAttacking(List("Alice", "Bob", "Charlie")) should be(None)
      }

      "repeat the prompt until a valid input is provided" in {
        val tui = Tui(MockController(Status()), MockRunner(List("x", "y", "Charlie")))

        tui.askForAttacking(List("Alice", "Bob", "Charlie")) should be(Some("Charlie"))
      }
    }

    "askForCard(String, List[Card], Boolean)" should {
      "return the selected card when a valid input is provided" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("1")))

        tui.askForCard("", cards, cancel = false) should be(Some(cards.head))
      }

      "return None when the user chooses to cancel" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("a")))

        tui.askForCard("", cards, cancel = true) should be(None)
      }

      "repeat the prompt until a valid input is provided" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("3", "0", "2")))

        tui.askForCard("", cards, cancel = false) should be(Some(cards(1)))
      }
    }

    "askForAttack(Player, List[Card], List[Card], () => Unit, Card => Boolean)" should {
      "return the selected card when a valid input is provided" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("1")))

        var chosenCard: Option[Card] = None

        tui.askForAttack(Player("Player1", cards, Turn.FirstlyAttacking), cards, Nil, () => {}, card => {
          chosenCard = Some(card)
          true
        })

        chosenCard should be(Some(cards.head))
      }

      "disallow canceling the attack if both the defended or undefended list are empty" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("a", "1")))

        var chosenCard: Option[Card] = None

        tui.askForAttack(Player("Player1", cards, Turn.FirstlyAttacking), Nil, Nil, () => {}, card => {
          chosenCard = Some(card)
          true
        })

        chosenCard should be(Some(cards.head))
      }

      "cancel the attack when the user chooses to cancel" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("a")))

        var canceled = false

        tui.askForAttack(Player("Player1", cards, Turn.FirstlyAttacking), cards, Nil, () => canceled = true, _ => true)

        canceled should be(true)
      }

      "repeat the prompt until the chosen card is valid" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("1", "1")))

        var count = 0

        var chosenCard: Option[Card] = None

        tui.askForAttack(Player("Player1", cards, Turn.FirstlyAttacking), cards, Nil, () => {}, card => {
          count = count + 1

          if (count == 2) {
            chosenCard = Some(card)

            true
          } else {
            false
          }
        })

        chosenCard should be(Some(cards.head))
      }
    }

    "askForDefend(Player, List[Card], List[Card], () => Unit, (Card, Card) => Boolean)" should {
      "return the selected cards when a valid input is provided" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("1", "2")))

        var chosenUsed: Option[Card] = None
        var chosenUndefended: Option[Card] = None

        tui.askForDefend(Player("Player1", cards, Turn.FirstlyAttacking), cards, cards, () => {}, (used, undefended) => {
          chosenUsed = Some(used)
          chosenUndefended = Some(undefended)

          true
        })

        chosenUsed should be(Some(cards.last))
        chosenUndefended should be(Some(cards.head))
      }

      "cancel the defend when the user cancels instantly" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("a")))

        var canceled = false

        tui.askForDefend(Player("Player1", cards, Turn.FirstlyAttacking), cards, cards, () => canceled = true, (_, _) => true)

        canceled should be(true)
      }

      "cancel the defend when the user chooses a card and then cancels" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("1", "a")))

        var canceled = false

        tui.askForDefend(Player("Player1", cards, Turn.FirstlyAttacking), cards, cards, () => canceled = true, (_, _) => true)

        canceled should be(true)
      }

      "repeat the prompt until the chosen cards are valid" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
        val tui = Tui(MockController(Status()), MockRunner(List("1", "2", "1", "2")))

        var count = 0

        var chosenUsed: Option[Card] = None
        var chosenUndefended: Option[Card] = None

        tui.askForDefend(Player("Player1", cards, Turn.FirstlyAttacking), cards, cards, () => {}, (used, undefended) => {
          count = count + 1

          if (count == 2) {
            chosenUsed = Some(used)
            chosenUndefended = Some(undefended)

            true
          } else {
            false
          }
        })

        chosenUsed should be(Some(cards.last))
        chosenUndefended should be(Some(cards.head))
      }
    }
  }
}