import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {

  "Card" should {
    "beats(Card, Card)" should {
      "correctly determine if it beats another card of the same suit" in {
        val card1 = Card(Rank.Ace, Suit.Spades)
        val card2 = Card(Rank.King, Suit.Spades)
        card1.beats(card2) should be(true)
        card2.beats(card1) should be(false)
      }

      "correctly determine if it does not beat another card of a different suit" in {
        val card1 = Card(Rank.Ace, Suit.Spades)
        val card2 = Card(Rank.King, Suit.Hearts)
        card1.beats(card2) should be(false)
        card2.beats(card1) should be(false)
      }
    }
  }

}