package controller.base.command

import com.google.inject.Guice
import controller.base.BaseController
import model.io.JsonFileIo
import model.status.Status
import model.*
import module.DurakModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PickUpCommandSpec extends AnyWordSpec with Matchers {

  "PickUpCommand" should {
    "execute()" should {
      "fill up the defending player's cards with the used, defended and undefended cards" in {
        val controller = BaseController(JsonFileIo())

        controller.status = Status(List(Player("Player1", List(Card(Rank.Seven, Suit.Spades)), Turn.Defending), Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Spades)), List(Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades)), List(Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades)), false, None)

        PickUpCommand(controller).execute()

        controller.status.players.head.cards should contain allElementsOf List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades), Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades))
      }

      "set the turn to FirstlyAttacking and reset the defended, undefended and used List" in {
        val controller = BaseController(JsonFileIo())

        controller.status = Status(List(Player("Player1", List(Card(Rank.Seven, Suit.Spades)), Turn.Defending), Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Spades)), List(Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades)), List(Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades)), false, None)

        PickUpCommand(controller).execute()

        controller.status.turn should be(Turn.FirstlyAttacking)
        controller.status.defended should be(empty)
        controller.status.undefended should be(empty)
        controller.status.used should be(empty)
      }
    }
  }
}
