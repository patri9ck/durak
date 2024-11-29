package controller.base.command

import controller.base.BaseController
import model.{Card, Player, Rank, Status, Suit, Turn}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AttackCommandSpec extends AnyWordSpec with Matchers {
  "AttackCommand" should {
    "doStep()" should {
      "remove the card from the attacking player and add it to the undefended cards" in {
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades), Card(Rank.Seven, Suit.Spades)), Turn.FirstlyAttacking),
          Player("Player2", Nil, Turn.Defending)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        AttackCommand(controller, Card(Rank.Ace, Suit.Spades)).doStep()

        controller.status.players.head.cards.size should be(1)
        controller.status.undefended should contain(Card(Rank.Ace, Suit.Spades))
      }

      "set the turn to Defending if there is only one attacking player" in {
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.FirstlyAttacking),
          Player("Player2", Nil, Turn.Defending)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        AttackCommand(controller, Card(Rank.Ace, Suit.Spades)).doStep()

        controller.status.turn should be(Turn.Defending)
      }

      "set the turn to Defending if the attacking player is SecondlyAttacking" in {
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.SecondlyAttacking),
          Player("Player2", Nil, Turn.Defending), Player("Player3", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.SecondlyAttacking, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        AttackCommand(controller, Card(Rank.Ace, Suit.Spades)).doStep()

        controller.status.turn should be(Turn.Defending)
      }

      "set the turn to SecondlyAttacking if the attacking player is FirstlyAttacking" in {
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.SecondlyAttacking),
          Player("Player2", Nil, Turn.Defending), Player("Player3", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        AttackCommand(controller, Card(Rank.Ace, Suit.Spades)).doStep()

        controller.status.turn should be(Turn.SecondlyAttacking)
      }

      "set the attacking player to finished if he finished" in {
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.FirstlyAttacking),
          Player("Player2", Nil, Turn.Defending)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        AttackCommand(controller, Card(Rank.Ace, Suit.Spades)).doStep()

        controller.status.players.head.turn should be(Turn.Finished)
      }
    }
  }
}
