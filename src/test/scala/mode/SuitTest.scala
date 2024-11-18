import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class SuitSpec extends AnyWordSpec with Matchers {

  "A Suit" should {

    "have the correct display values" in {
      Suit.Spades.display should be("♠")
      Suit.Hearts.display should be("♥")
      Suit.Diamonds.display should be("♦")
      Suit.Clubs.display should be("♣")
    }

    "return a random suit" in {
      val suit = Suit.getRandomSuit
      Suit.values should contain(suit)
    }
  }
}