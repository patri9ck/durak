import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class RoundSpec extends AnyWordSpec with Matchers {

  "A Round" should {

    "correctly initialize with given turn, defended, undefended, used, passed, and denied" in {
      val turn = Turn.FirstlyAttacking
      val defended = List(Card(Rank.Ace, Suit.Spades))
      val undefended = List(Card(Rank.King, Suit.Hearts))
      val used = List(Card(Rank.Queen, Suit.Diamonds))
      val passed = Some(Player("Player1", List(), Turn.Watching))
      val denied = false

      val round = Round(turn, defended, undefended, used, denied, passed)

      round.turn should be(turn)
      round.defended should be(defended)
      round.undefended should be(undefended)
      round.used should be(used)
      round.passed should be(passed)
      round.denied should be(denied)
    }

    "allow updating the turn" in {
      val round = Round(Turn.FirstlyAttacking, List(), List(), List(), false, None)
      val newTurn = Turn.Defending

      val updatedRound = round.copy(turn = newTurn)

      updatedRound.turn should be(newTurn)
      updatedRound.defended should be(round.defended)
      updatedRound.undefended should be(round.undefended)
      updatedRound.used should be(round.used)
      updatedRound.passed should be(round.passed)
      updatedRound.denied should be(round.denied)
    }

    "allow updating the defended cards" in {
      val round = Round(Turn.FirstlyAttacking, List(), List(), List(), false, None)
      val newDefended = List(Card(Rank.Ace, Suit.Spades))

      val updatedRound = round.copy(defended = newDefended)

      updatedRound.turn should be(round.turn)
      updatedRound.defended should be(newDefended)
      updatedRound.undefended should be(round.undefended)
      updatedRound.used should be(round.used)
      updatedRound.passed should be(round.passed)
      updatedRound.denied should be(round.denied)
    }

    "allow updating the undefended cards" in {
      val round = Round(Turn.FirstlyAttacking, List(), List(), List(), false, None)
      val newUndefended = List(Card(Rank.King, Suit.Hearts))

      val updatedRound = round.copy(undefended = newUndefended)

      updatedRound.turn should be(round.turn)
      updatedRound.defended should be(round.defended)
      updatedRound.undefended should be(newUndefended)
      updatedRound.used should be(round.used)
      updatedRound.passed should be(round.passed)
      updatedRound.denied should be(round.denied)
    }

    "allow updating the used cards" in {
      val round = Round(Turn.FirstlyAttacking, List(), List(), List(), false, None)
      val newUsed = List(Card(Rank.Queen, Suit.Diamonds))

      val updatedRound = round.copy(used = newUsed)

      updatedRound.turn should be(round.turn)
      updatedRound.defended should be(round.defended)
      updatedRound.undefended should be(round.undefended)
      updatedRound.used should be(newUsed)
      updatedRound.passed should be(round.passed)
      updatedRound.denied should be(round.denied)
    }

    "allow updating the passed player" in {
      val round = Round(Turn.FirstlyAttacking, List(), List(), List(), false, None)
      val newPassed = Some(Player("Player1", List(), Turn.Watching))

      val updatedRound = round.copy(passed = newPassed)

      updatedRound.turn should be(round.turn)
      updatedRound.defended should be(round.defended)
      updatedRound.undefended should be(round.undefended)
      updatedRound.used should be(round.used)
      updatedRound.passed should be(newPassed)
      updatedRound.denied should be(round.denied)
    }

    "allow updating the denied status" in {
      val round = Round(Turn.FirstlyAttacking, List(), List(), List(), false, None)
      val newDenied = true

      val updatedRound = round.copy(denied = newDenied)

      updatedRound.turn should be(round.turn)
      updatedRound.defended should be(round.defended)
      updatedRound.undefended should be(round.undefended)
      updatedRound.used should be(round.used)
      updatedRound.passed should be(round.passed)
      updatedRound.denied should be(newDenied)
    }
  }
}