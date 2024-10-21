package card

import card.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SuitSpec extends AnyWordSpec with Matchers {

  "A Suit" should {

    "have the correct display values for each suit" in {
      Suit.Spades.display shouldBe "♠"
      Suit.Hearts.display shouldBe "♥"
      Suit.Diamonds.display shouldBe "♦"
      Suit.Clubs.display shouldBe "♣"
    }

    "generate a random suit with getRandomSuit" in {
      val randomSuit = getRandomSuit
      Suit.values should contain(randomSuit) // Ensure the random suit is one of the valid suits
    }

    "generate different random suits" in {
      val suit1 = getRandomSuit
      val suit2 = getRandomSuit
      suit1 should not equal suit2 // Although randomness might generate the same, we're testing for variability
    }
  }
}