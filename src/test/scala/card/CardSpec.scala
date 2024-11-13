package card

import model.{Card, Rank, Suit, getRandomCard, getRandomCards}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {

  "getRandomCard" should {
    "return a card with a valid rank and suit" in {
      val card = getRandomCard
      card.rank should not be null
      card.suit should not be null
    }
  }

  "getRandomCards" should {
    "return a list of the specified number of cards" in {
      val cards = getRandomCards(5)
      cards.size shouldEqual 5
      all(cards.map(_.rank)) should not be null
      all(cards.map(_.suit)) should not be null
    }

    "return a list with no duplicate cards if givenCards is provided" in {
      val givenCards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.Two, Suit.Hearts))
      val newCards = getRandomCards(3, givenCards)

      newCards.size shouldEqual 3
      newCards.foreach { card =>
        givenCards should not contain card
      }
    }
  }
}
