import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.*

import java.util.concurrent.CompletableFuture.anyOf

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {

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

    "return a random card" in {
      val card = Card.getRandomCard
      card shouldBe a[Card]
    }

    "return a list of random cards" in {
      val cards = Card.getRandomCards(5)
      cards should have length 5
      all(cards) shouldBe a[Card]
    }

    "return a list of random cards excluding given cards" in {
      val givenCards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
      val cards = Card.getRandomCards(5, givenCards)
      cards should have length 5
      all(cards) shouldBe a[Card]
      givenCards should not contain cards
    }
  }
}