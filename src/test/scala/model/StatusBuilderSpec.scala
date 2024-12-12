package model

import model.Turn.Watching
import model.status.StatusBuilder
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StatusBuilderSpec extends AnyWordSpec with Matchers {

  "StatusBuilder" should {
    "setPlayers(List[Player]) and getPlayers" should {
      "return StatusBuilder with players set" in {
        val players = List(Player("Player 1", List(), Turn.Watching), Player("Player 2", List(), Turn.Watching))
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setPlayers(players) should be(statusBuilder)
        statusBuilder.getPlayers should be(players)
      }
    }

    "setStack(List[Card]) and getStack" should {
      "return StatusBuilder with stack set" in {
        val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setStack(stack) should be(statusBuilder)
        statusBuilder.getStack should be(stack)
      }
    }

    "setTrump(Card) and getTrump" should {
      "return StatusBuilder with trump set" in {
        val trump = Card(Rank.Ace, Suit.Spades)
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setTrump(trump) should be(statusBuilder)
        statusBuilder.getTrump should be(Some(trump))
      }
    }

    "setAmount(Int) and getAmount" should {
      "return StatusBuilder with amount set" in {
        val amount = 10
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setAmount(amount) should be(statusBuilder)
        statusBuilder.getAmount should be(amount)
      }
    }

    "setTurn(Turn) and getTurn" should {
      "return StatusBuilder with turn set" in {
        val turn = Turn.FirstlyAttacking
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setTurn(turn) should be(statusBuilder)
        statusBuilder.getTurn should be(turn)
      }
    }

    "setDefended(List[Card]) and getDefended" should {
      "return StatusBuilder with defended set" in {
        val defended = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setDefended(defended) should be(statusBuilder)
        statusBuilder.getDefended should be(defended)
      }
    }

    "setUndefended(List[Card]) and setUndefended" should {
      "return StatusBuilder with undefended set" in {
        val undefended = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setUndefended(undefended) should be(statusBuilder)
        statusBuilder.getUndefended should be(undefended)
      }
    }

    "setUsed(List[Card]) and getUsed" should {
      "return StatusBuilder with used set" in {
        val used = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setUsed(used) should be(statusBuilder)
        statusBuilder.getUsed should be(used)
      }
    }

    "resetRound" should {
      "return StatusBuilder with turn set to FirstlyAttacking, defended, undefended and used set to empty lists and denied set to false" in {
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)
        statusBuilder.setTurn(Turn.Defending)
        statusBuilder.setDefended(List(Card(Rank.Ace, Suit.Spades)))
        statusBuilder.setUndefended(List(Card(Rank.King, Suit.Spades)))
        statusBuilder.setUsed(List(Card(Rank.Queen, Suit.Spades)))
        statusBuilder.setDenied(true)

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
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setDenied(true) should be(statusBuilder)
        statusBuilder.isDenied should be(true)
      }
    }

    "setPassed(Player) and getPassed" should {
      "return StatusBuilder with passed set" in {
        val passed = Player("Player 1", List(), Watching)
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, None)

        statusBuilder.setPassed(passed) should be(statusBuilder)
        statusBuilder.getPassed should be(Some(passed))
      }
    }

    "removePassed()" should {
      "return StatusBuilder with passed set to None" in {
        val passed = Player("Player 1", List(), Watching)
        val statusBuilder = StatusBuilder(List(), List(), Some(Card(Rank.Two, Suit.Diamonds)), 6, Turn.Watching, List(), List(), List(), false, Some(passed))

        statusBuilder.removePassed() should be(statusBuilder)
        statusBuilder.getPassed should be(None)
      }
    }
  }
}
