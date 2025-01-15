package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import scala.xml.Utility

class CardSpec extends AnyWordSpec with Matchers {

  "Card" should {
    "beats(Card, Card)" should {
      "return true if it beats another card of the same suit" in {
        val card1 = Card(Rank.Ace, Suit.Spades)
        val card2 = Card(Rank.King, Suit.Spades)
        card1.beats(card2) should be(true)
        card2.beats(card1) should be(false)
      }

      "return false if both cards have different suits" in {
        val card1 = Card(Rank.Ace, Suit.Spades)
        val card2 = Card(Rank.King, Suit.Hearts)
        card1.beats(card2) should be(false)
        card2.beats(card1) should be(false)
      }
    }

    "toString" should {
      "return the card's rank and suit" in {
        val card = Card(Rank.Ace, Suit.Spades)

        card.toString should be(
          """┌─────┐
            |│A    │
            |│  ♠  │
            |│    A│
            |└─────┘""".stripMargin)
      }
    }

    "getPath" should {
      "return the path to the card's image" in {
        val card = Card(Rank.Ace, Suit.Spades)

        card.getPath should be(getClass.getResource("/cards/AS.png").toString)
      }
    }

    "toSelectableCard" should {
      "return a SelectableCard with selected set to false" in {
        val card = Card(Rank.Ace, Suit.Spades)
        val selectableCard = card.toSelectableCard

        selectableCard.card should be(card)
        selectableCard.selected should be(false)
      }
    }

    "toXml" should {
      "convert a Card into XML" in {
        val xml =
          <card>
            <rank>{Rank.Ace}</rank>
            <suit>{Suit.Spades}</suit>
          </card>

        Utility.trim(Card(Rank.Ace, Suit.Spades).toXml).toString should be(Utility.trim(xml).toString)
      }
    }

    "getDeck" should {
      "return a shuffled deck of cards" in {
        val deck = Card.getDeck

        deck.distinct.size should be(52)
      }
    }

    "toSelectableCards(List[Card]" should {
      "return a list of SelectableCards with selected set to false" in {
        val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))
        val selectableCards = Card.toSelectableCards(cards)

        selectableCards.map(_.card) should be(cards)
        selectableCards.forall(!_.selected) should be(true)
      }
    }
    
    "cardFormat" should {
      "convert a Card into JSON and get a Card from JSON" in {
        val json = """{"rank":"Ace","suit":"Spades"}"""
        
        val card = Card(Rank.Ace, Suit.Spades)
        
        Card.cardFormat.writes(card) should be(Json.parse(json))
        Card.cardFormat.reads(Json.parse(json)).get should be(card)
      }
    }
    
    "fromXml(Node)" should {
      "get a Card from XML" in {
        val xml =
          <card>
            <rank>Ace</rank> 
            <suit>Spades</suit>
          </card>

        Card.fromXml(xml) should be(Card(Rank.Ace, Suit.Spades))
      }
    }
  }
}