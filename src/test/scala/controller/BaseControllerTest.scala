import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._
import controller.base.BaseController

class BaseControllerSpec extends AnyWordSpec with Matchers {

  "A BaseController" should {

    "choose the correct defending player" in {
      val players = List(
        Player("Player1", List(), Turn.Watching),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val status = Status(Group(players, List(), 6), Round(Turn.Watching, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.chooseDefending(players(1))
      controller.status.group.players(1).turn should be (Turn.Defending)
    }

    "choose a random defending player" in {
      val players = List(
        Player("Player1", List(), Turn.Watching),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val status = Status(Group(players, List(), 6), Round(Turn.Watching, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.chooseDefending()
      controller.status.group.players.exists(_.turn == Turn.Defending) should be (true)
    }

    "allow a valid attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val player = Player("Player1", List(card), Turn.FirstlyAttacking)
      val status = Status(Group(List(player), List(), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.canAttack(card) should be (true)
    }

    "deny an invalid attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val player = Player("Player1", List(card), Turn.Defending)
      val status = Status(Group(List(player), List(), 6), Round(Turn.Defending, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.canAttack(card) should be (false)
    }

    "allow a valid defense" in {
      val card1 = Card(Rank.Ace, Suit.Spades)
      val card2 = Card(Rank.King, Suit.Spades)
      val player = Player("Player1", List(card2), Turn.Defending)
      val status = Status(Group(List(player), List(), 6), Round(Turn.Defending, List(card1), List(), List(), false, None))
      val controller = BaseController(status)

      controller.canDefend(card2, card1) should be (true)
    }

    "deny an invalid defense" in {
      val card1 = Card(Rank.Ace, Suit.Spades)
      val card2 = Card(Rank.Two, Suit.Spades)
      val player = Player("Player1", List(card2), Turn.Defending)
      val status = Status(Group(List(player), List(), 6), Round(Turn.Defending, List(card1), List(), List(), false, None))
      val controller = BaseController(status)

      controller.canDefend(card2, card1) should be (false)
    }

    "handle denied attack" in {
      val player = Player("Player1", List(), Turn.FirstlyAttacking)
      val status = Status(Group(List(player), List(), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.denied()
      controller.status.round.denied should be (true)
    }

    "handle pick up" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val player = Player("Player1", List(card), Turn.Defending)
      val status = Status(Group(List(player), List(), 6), Round(Turn.Defending, List(card), List(), List(), false, None))
      val controller = BaseController(status)

      controller.pickUp()
      controller.status.round.undefended should be (empty)
    }

    "handle attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val player = Player("Player1", List(card), Turn.FirstlyAttacking)
      val status = Status(Group(List(player), List(), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.attack(card)
      controller.status.round.undefended should contain (card)
    }

    "handle defense" in {
      val card1 = Card(Rank.Ace, Suit.Spades)
      val card2 = Card(Rank.King, Suit.Spades)
      val player = Player("Player1", List(card2), Turn.Defending)
      val status = Status(Group(List(player), List(), 6), Round(Turn.Defending, List(card1), List(), List(), false, None))
      val controller = BaseController(status)

      controller.defend(card2, card1)
      controller.status.round.defended should contain (card1)
    }

    "find player by turn" in {
      val player = Player("Player1", List(), Turn.FirstlyAttacking)
      val status = Status(Group(List(player), List(), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.byTurn(Turn.FirstlyAttacking) should be (Some(player))
    }
  }
}