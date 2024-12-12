package controller.base.command

import controller.base.BaseController
import model.status.Status
import model.{Card, Player, Rank, Suit, Turn}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DefendCommandSpec extends AnyWordSpec with Matchers {
  "DefendCommand" should {
    "doStep()" should {
      "remove the used card from the defending player and add it to the used cards and take the undefended to the defended" in {
        val status = model.status.Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades), Card(Rank.Seven, Suit.Spades)), Turn.Defending),
          Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, Nil, List(Card(Rank.King, Suit.Spades), Card(Rank.Two, Suit.Hearts)), Nil, false, None)
        val controller = BaseController(status)

        DefendCommand(controller, Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)).doStep()

        controller.status.players.head.cards.size should be(1)
        controller.status.used should contain(Card(Rank.Ace, Suit.Spades))
        controller.status.defended should contain(Card(Rank.King, Suit.Spades))
        controller.status.undefended should not contain Card(Rank.King, Suit.Spades)
      }

      "set the turn to FirstlyAttacking" in {
        val status = model.status.Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades), Card(Rank.Seven, Suit.Spades)), Turn.Defending),
          Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, Nil, List(Card(Rank.King, Suit.Spades), Card(Rank.Two, Suit.Hearts)), Nil, false, None)
        val controller = BaseController(status)

        DefendCommand(controller, Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)).doStep()

        controller.status.turn should be(Turn.FirstlyAttacking)
      }

      "set the defending player to finished if he finished" in {
        val status = model.status.Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Defending),
          Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, Nil, List(Card(Rank.King, Suit.Spades)), Nil, false, None)
        val controller = BaseController(status)

        DefendCommand(controller, Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)).doStep()

        controller.status.players.head.turn should be(Turn.Finished)
      }
    }
  }

}
