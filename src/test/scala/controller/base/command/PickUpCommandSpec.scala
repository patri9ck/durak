package controller.base.command

import controller.base.BaseController
import model.status.Status
import model.{Card, Player, Rank, Suit, Turn}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PickUpCommandSpec extends AnyWordSpec with Matchers {
  "PickUpCommand" should {
    "doStep()" should {
      "fill up the defending player's cards with the used, defended and undefended cards" in {
        val status = model.status.Status(List(Player("Player1", List(Card(Rank.Seven, Suit.Spades)), Turn.Defending),
          Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Spades)), List(Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades)), List(Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades)), false, None)
        val controller = BaseController(status)

        PickUpCommand(controller).doStep()

        controller.status.players.head.cards should contain allElementsOf List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades), Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades))
      }

      "set the turn to FirstlyAttacking and reset the defended, undefended and used List" in {
        val status = model.status.Status(List(Player("Player1", List(Card(Rank.Seven, Suit.Spades)), Turn.Defending),
          Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Spades)), List(Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades)), List(Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades)), false, None)
        val controller = BaseController(status)

        PickUpCommand(controller).doStep()

        controller.status.turn should be(Turn.FirstlyAttacking)
        controller.status.defended should be(empty)
        controller.status.undefended should be(empty)
        controller.status.used should be(empty)
      }
    }
  }
}
