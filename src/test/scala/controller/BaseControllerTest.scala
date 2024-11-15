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
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Watching, List(), List(), List(), None, false))
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
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Watching, List(), List(), List(), None, false))
      val controller = BaseController(status)

      controller.chooseDefending()
      controller.status.group.players.exists(_.turn == Turn.Defending) should be (true)
    }

    "allow a valid attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.FirstlyAttacking),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )

      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.FirstlyAttacking, List(), List(), List(), None, false))
      val controller = BaseController(status)

      controller.canAttack(card) should be (true)
    }

    "deny an invalid attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Defending, List(), List(), List(), None, false))
      val controller = BaseController(status)

      controller.canAttack(card) should be (false)
    }

    "allow a valid defense" in {
      val card1 = Card(Rank.Ace, Suit.Spades)
      val card2 = Card(Rank.King, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Defending, List(card1), List(), List(), None, false))
      val controller = BaseController(status)

      controller.canDefend(card2, card1) should be (true)
    }

    "deny an invalid defense" in {
      val card1 = Card(Rank.Ace, Suit.Spades)
      val card2 = Card(Rank.Two, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(List(), stack, trump,3 ), Round(Turn.Defending, List(card1), List(), List(), None, false))
      val controller = BaseController(status)

      controller.canDefend(card2, card1) should be (false)
    }

    "handle denied attack" in {
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.FirstlyAttacking),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val status = Status(Group(players, stack, trump,3), Round(Turn.FirstlyAttacking, List(), List(), List(), None, false))
      val controller = BaseController(status)

      controller.denied()
      controller.status.round.denied should be (true)
    }

    "handle pick up" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Defending, List(card), List(), List(), None, false))
      val controller = BaseController(status)

      controller.pickUp()
      controller.status.round.undefended should be (empty)
    }

    "handle attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.FirstlyAttacking),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.FirstlyAttacking, List(), List(), List(), None, false))
      val controller = BaseController(status)

      controller.attack(card)
      controller.status.round.undefended should contain (card)
    }

    "handle defense" in {
      val card1 = Card(Rank.Ace, Suit.Spades)
      val card2 = Card(Rank.King, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Defending, List(card1), List(), List(), None, false))
      val controller = BaseController(status)

      controller.defend(card2, card1)
      controller.status.round.defended should contain (card1)
    }

    "find player by turn" in {
      val players = List(
        Player("Player1", List(), Turn.FirstlyAttacking),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.FirstlyAttacking, List(), List(), List(), None, false))
      val controller = BaseController(status)

      controller.byTurn(Turn.FirstlyAttacking) should be (Some(players))
    }
  }
}