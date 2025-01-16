package model.status

import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ImmutableStatusBuilderSpec extends AnyWordSpec with Matchers {

  "ImmutableStatusBuilder" should {
    "setStatus(Status)" should {
      "return a new ImmutableStatusBuilder with status set" in {
        val status = Status(List(Player("Player 1", List(), Turn.Watching), Player("Player 2", List(), Turn.Watching)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), Some(Card(Rank.Ace, Suit.Spades)), 10, Turn.FirstlyAttacking, List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), false, Some(Player("Player 1", List(), Turn.Watching)))
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setStatus(status)

        updatedStatusBuilder.getPlayers should be(status.players)
        updatedStatusBuilder.getStack should be(status.stack)
        updatedStatusBuilder.getTrump should be(status.trump)
        updatedStatusBuilder.getAmount should be(status.amount)
        updatedStatusBuilder.getTurn should be(status.turn)
        updatedStatusBuilder.getDefended should be(status.defended)
        updatedStatusBuilder.getUndefended should be(status.undefended)
        updatedStatusBuilder.getUsed should be(status.used)
        updatedStatusBuilder.isDenied should be(status.denied)
        updatedStatusBuilder.getPassed should be(status.passed)

        statusBuilder.getPlayers should be(Nil)
        statusBuilder.getStack should be(Nil)
        statusBuilder.getTrump should be(None)
        statusBuilder.getAmount should be(0)
        statusBuilder.getTurn should be(Turn.Uninitialized)
        statusBuilder.getDefended should be(Nil)
        statusBuilder.getUndefended should be(Nil)
        statusBuilder.getUsed should be(Nil)
        statusBuilder.isDenied should be(false)
        statusBuilder.getPassed should be(None)
      }
    }

    "setPlayers(List[Player]) and getPlayers" should {
      "return a new ImmutableStatusBuilder with players set" in {
        val players = List(Player("Player 1", List(), Turn.Watching), Player("Player 2", List(), Turn.Watching))
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setPlayers(players)

        updatedStatusBuilder.getPlayers should be(players)
        statusBuilder.getPlayers should be(Nil)
      }
    }

    "setStack(List[Card]) and getStack" should {
      "return a new ImmutableStatusBuilder with stack set" in {
        val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setStack(stack)

        updatedStatusBuilder.getStack should be(stack)
        statusBuilder.getStack should be(Nil)
      }
    }

    "setTrump(Card) and getTrump" should {
      "return a new ImmutableStatusBuilder with trump set" in {
        val trump = Card(Rank.Ace, Suit.Spades)
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setTrump(trump)

        updatedStatusBuilder.getTrump should be(Some(trump))
        statusBuilder.getTrump should be(None)
      }
    }

    "removeTrump" should {
      "return a new ImmutableStatusBuilder with trump set to None" in {
        val statusBuilder = new ImmutableStatusBuilder(trump = Some(Card(Rank.Two, Suit.Diamonds)))

        val updatedStatusBuilder = statusBuilder.removeTrump()

        updatedStatusBuilder.getTrump should be(None)
        statusBuilder.getTrump should be(Some(Card(Rank.Two, Suit.Diamonds)))
      }
    }

    "setAmount(Int) and getAmount" should {
      "return a new ImmutableStatusBuilder with amount set" in {
        val amount = 10
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setAmount(amount)

        updatedStatusBuilder.getAmount should be(amount)
        statusBuilder.getAmount should be(0)
      }
    }

    "setTurn(Turn) and getTurn" should {
      "return a new ImmutableStatusBuilder with turn set" in {
        val turn = Turn.FirstlyAttacking
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setTurn(turn)

        updatedStatusBuilder.getTurn should be(turn)
        statusBuilder.getTurn should be(Turn.Uninitialized)
      }
    }

    "setDefended(List[Card]) and getDefended" should {
      "return a new ImmutableStatusBuilder with defended set" in {
        val defended = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setDefended(defended)

        updatedStatusBuilder.getDefended should be(defended)
        statusBuilder.getDefended should be(Nil)
      }
    }

    "setUndefended(List[Card]) and getUndefended" should {
      "return a new ImmutableStatusBuilder with undefended set" in {
        val undefended = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setUndefended(undefended)

        updatedStatusBuilder.getUndefended should be(undefended)
        statusBuilder.getUndefended should be(Nil)
      }
    }

    "setUsed(List[Card]) and getUsed" should {
      "return a new ImmutableStatusBuilder with used set" in {
        val used = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setUsed(used)

        updatedStatusBuilder.getUsed should be(used)
        statusBuilder.getUsed should be(Nil)
      }
    }

    "resetRound" should {
      "return a new ImmutableStatusBuilder with round reset" in {
        val statusBuilder = new ImmutableStatusBuilder(
          turn = Turn.Defending,
          defended = List(Card(Rank.Ace, Suit.Spades)),
          undefended = List(Card(Rank.King, Suit.Spades)),
          used = List(Card(Rank.Queen, Suit.Spades)),
          denied = true
        )

        val updatedStatusBuilder = statusBuilder.resetRound

        updatedStatusBuilder.getTurn should be(Turn.FirstlyAttacking)
        updatedStatusBuilder.getDefended should be(Nil)
        updatedStatusBuilder.getUndefended should be(Nil)
        updatedStatusBuilder.getUsed should be(Nil)
        updatedStatusBuilder.isDenied should be(false)

        statusBuilder.getTurn should be(Turn.Defending)
        statusBuilder.getDefended should be(List(Card(Rank.Ace, Suit.Spades)))
      }
    }

    "setDenied(Boolean) and isDenied" should {
      "return a new ImmutableStatusBuilder with denied set" in {
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setDenied(true)

        updatedStatusBuilder.isDenied should be(true)
        statusBuilder.isDenied should be(false)
      }
    }

    "setPassed(Player) and getPassed" should {
      "return a new ImmutableStatusBuilder with passed set" in {
        val passed = Player("Player 1", List(), Turn.Watching)
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setPassed(passed)

        updatedStatusBuilder.getPassed should be(Some(passed))
        statusBuilder.getPassed should be(None)
      }
    }

    "removePassed" should {
      "return a new ImmutableStatusBuilder with passed set to None" in {
        val passed = Player("Player 1", List(), Turn.Watching)
        val statusBuilder = new ImmutableStatusBuilder(passed = Some(passed))

        val updatedStatusBuilder = statusBuilder.removePassed()

        updatedStatusBuilder.getPassed should be(None)
        statusBuilder.getPassed should be(Some(passed))
      }
    }

    "byTurn" should {
      "return a Player with the given turn" in {
        val player = Player("Player 1", List(), Turn.Watching)
        val statusBuilder = new ImmutableStatusBuilder(players = List(player))

        statusBuilder.byTurn(Turn.Watching) should be(Some(player))
        statusBuilder.byTurn(Turn.FirstlyAttacking) should be(None)
      }
    }

    "status" should {
      "return a new Status with the current status" in {
        val status = Status(List(Player("Player 1", List(), Turn.Watching), Player("Player 2", List(), Turn.Watching)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), Some(Card(Rank.Ace, Suit.Spades)), 10, Turn.FirstlyAttacking, List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)), false, Some(Player("Player 1", List(), Turn.Watching)))
        val statusBuilder = new ImmutableStatusBuilder()

        val updatedStatusBuilder = statusBuilder.setStatus(status)

        updatedStatusBuilder.status should be(status)
        statusBuilder.status should be(Status())
      }
    }
  }
}
