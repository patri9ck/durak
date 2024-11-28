package controller.base.command

import controller.base.BaseController
import model.{Card, Player, Rank, Status, Suit, Turn}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DenyCommandSpec extends AnyWordSpec with Matchers {
  "DenyCommand" should {
    "doStep()" should {
      "set the defending player to the attacking player if all cards are defended and all attacking players denied" in {
        val players = List(
          Player("Player1", Nil, Turn.SecondlyAttacking),
          Player("Player2", Nil, Turn.Defending),
          Player("Player3", Nil, Turn.FirstlyAttacking)
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.SecondlyAttacking, Nil, Nil, Nil, true, None)
        val controller = BaseController(status)

        DenyCommand(controller).doStep()

        controller.status.players(1).turn should be(Turn.FirstlyAttacking)
      }

      "set the turn to Defending if both attacking players denied and there are still undefended cards" in {
        val players = List(
          Player("Player1", Nil, Turn.SecondlyAttacking),
          Player("Player2", Nil, Turn.Defending),
          Player("Player3", Nil, Turn.FirstlyAttacking)
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.SecondlyAttacking, Nil, List(Card(Rank.Ace, Suit.Spades)), Nil, true, None)
        val controller = BaseController(status)

        DenyCommand(controller).doStep()

        controller.status.turn should be(Turn.Defending)
      }

      "set the turn to SecondlyAttacking if there are two attacking players and the first one denied" in {
        val players = List(
          Player("Player1", Nil, Turn.SecondlyAttacking),
          Player("Player2", Nil, Turn.Defending),
          Player("Player3", Nil, Turn.FirstlyAttacking)
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.FirstlyAttacking, Nil, List(Card(Rank.Ace, Suit.Spades)), Nil, false, None)
        val controller = BaseController(status)

        DenyCommand(controller).doStep()

        controller.status.turn should be(Turn.SecondlyAttacking)
      }
    }
  }
}
