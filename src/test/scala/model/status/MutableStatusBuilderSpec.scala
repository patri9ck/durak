package model.status

import model.*
import model.Suit.Spades
import model.status.{MutableStatusBuilder, StatusBuilder}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MutableStatusBuilderSpec extends AnyWordSpec with Matchers {

  "StatusBuilder" should {
    "setStatus(Status)" should {
      "return StatusBuilder with status set" in {
        val status = Status(List(Player("Player 1", List(), Turn.Watching), Player("Player 2", List(), Turn.Watching)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), Some(Card(Rank.Ace, Suit.Spades)), 10, Turn.FirstlyAttacking, List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), false, Some(Player("Player 1", List(), Turn.Watching)))
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setStatus(status) should be(statusBuilder)
        statusBuilder.getPlayers should be(status.players)
        statusBuilder.getStack should be(status.stack)
        statusBuilder.getTrump should be(status.trump)
        statusBuilder.getAmount should be(status.amount)
        statusBuilder.getTurn should be(status.turn)
        statusBuilder.getDefended should be(status.defended)
        statusBuilder.getUndefended should be(status.undefended)
        statusBuilder.getUsed should be(status.used)
        statusBuilder.isDenied should be(status.denied)
        statusBuilder.getPassed should be(status.passed)
      }
    }

    "setPlayers(List[Player]) and getPlayers" should {
      "return StatusBuilder with players set" in {
        val players = List(Player("Player 1", List(), Turn.Watching), Player("Player 2", List(), Turn.Watching))
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setPlayers(players) should be(statusBuilder)
        statusBuilder.getPlayers should be(players)
      }
    }

    "setStack(List[Card]) and getStack" should {
      "return StatusBuilder with stack set" in {
        val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setStack(stack) should be(statusBuilder)
        statusBuilder.getStack should be(stack)
      }
    }

    "setTrump(Card) and getTrump" should {
      "return StatusBuilder with trump set" in {
        val trump = Card(Rank.Ace, Suit.Spades)
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setTrump(trump) should be(statusBuilder)
        statusBuilder.getTrump should be(Some(trump))
      }
    }

    "removeTrump" should {
      "return StatusBuilder with trump set to None" in {
        val statusBuilder = MutableStatusBuilder(trump = Some(Card(Rank.Two, Suit.Diamonds)))

        statusBuilder.removeTrump() should be(statusBuilder)
        statusBuilder.getTrump should be(None)
      }

    }

    "setAmount(Int) and getAmount" should {
      "return StatusBuilder with amount set" in {
        val amount = 10
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setAmount(amount) should be(statusBuilder)
        statusBuilder.getAmount should be(amount)
      }
    }

    "setTurn(Turn) and getTurn" should {
      "return StatusBuilder with turn set" in {
        val turn = Turn.FirstlyAttacking
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setTurn(turn) should be(statusBuilder)
        statusBuilder.getTurn should be(turn)
      }
    }

    "setDefended(List[Card]) and getDefended" should {
      "return StatusBuilder with defended set" in {
        val defended = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setDefended(defended) should be(statusBuilder)
        statusBuilder.getDefended should be(defended)
      }
    }

    "setUndefended(List[Card]) and setUndefended" should {
      "return StatusBuilder with undefended set" in {
        val undefended = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setUndefended(undefended) should be(statusBuilder)
        statusBuilder.getUndefended should be(undefended)
      }
    }

    "setUsed(List[Card]) and getUsed" should {
      "return StatusBuilder with used set" in {
        val used = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setUsed(used) should be(statusBuilder)
        statusBuilder.getUsed should be(used)
      }
    }

    "resetRound" should {
      "return StatusBuilder with turn set to FirstlyAttacking, defended, undefended and used set to empty lists and denied set to false" in {
        val statusBuilder = MutableStatusBuilder(turn = Turn.Defending, defended = List(Card(Rank.Ace, Suit.Spades)), undefended = List(Card(Rank.King, Suit.Spades)), used = List(Card(Rank.Queen, Suit.Spades)), denied = true)

        statusBuilder.resetRound should be(statusBuilder)
        statusBuilder.getTurn should be(Turn.FirstlyAttacking)
        statusBuilder.getDefended should be(List())
        statusBuilder.getUndefended should be(List())
        statusBuilder.getUsed should be(List())
        statusBuilder.isDenied should be(false)
      }
    }

    "setDenied(Boolean) and isDenied" should {
      "return StatusBuilder with denied set" in {
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setDenied(true) should be(statusBuilder)
        statusBuilder.isDenied should be(true)
      }
    }

    "setPassed(Player) and getPassed" should {
      "return StatusBuilder with passed set" in {
        val passed = Player("Player 1", List(), Turn.Watching)
        val statusBuilder = MutableStatusBuilder()

        statusBuilder.setPassed(passed) should be(statusBuilder)
        statusBuilder.getPassed should be(Some(passed))
      }
    }

    "removePassed()" should {
      "return StatusBuilder with passed set to None" in {
        val statusBuilder = MutableStatusBuilder(passed = Some(Player("Player 1", List(), Turn.Watching)))

        statusBuilder.removePassed() should be(statusBuilder)
        statusBuilder.getPassed should be(None)
      }
    }

    "byTurn" should {
      "return a Player with the given turn" in {
        val player = Player("Player 1", List(), Turn.Watching)
        val statusBuilder = new MutableStatusBuilder(players = List(player))

        statusBuilder.byTurn(Turn.Watching) should be(Some(player))
        statusBuilder.byTurn(Turn.FirstlyAttacking) should be(None)
      }
    }

    "status" should {
      "return a new Status with the current status" in {
        val status = Status(List(Player("Player 1", List(), Turn.Watching), Player("Player 2", List(), Turn.Watching)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), Some(Card(Rank.Ace, Suit.Spades)), 10, Turn.FirstlyAttacking, List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), false, Some(Player("Player 1", List(), Turn.Watching)))
        val statusBuilder = new MutableStatusBuilder()

        statusBuilder.setStatus(status) should be(statusBuilder)
        statusBuilder.status should be(status)
      }
    }
  }
}
