package round

import card.*
import model.{Card, Rank, Suit, Turn, getNewPlayer}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {

  "getNewPlayer" should {

    "create a new Player with the correct name, cards, and Watching turn" in {
      val testCards = List(
        Card(Rank.Ace, Suit.Hearts),
        Card(Rank.Two, Suit.Diamonds),
        Card(Rank.Three, Suit.Clubs)
      )
      val cardGenerator: (Int, List[Card]) => List[Card] = (amount, cards) => cards.take(amount)

      val player = getNewPlayer("Alice", 2, testCards, cardGenerator)

      player.name shouldEqual "Alice"
      player.cards should have length 2
      player.cards shouldEqual List(
        Card(Rank.Ace, Suit.Hearts),
        Card(Rank.Two, Suit.Diamonds)
      )
      player.turn shouldEqual Turn.Watching
    }

    "create a Player with an empty card list if no cards are given" in {
      val emptyCards = List.empty[Card]
      val cardGenerator: (Int, List[Card]) => List[Card] = (amount, cards) => cards.take(amount)

      val player = getNewPlayer("Bob", 3, emptyCards, cardGenerator)

      player.name shouldEqual "Bob"
      player.cards shouldBe empty
      player.turn shouldEqual Turn.Watching
    }

    "limit the number of cards generated if there are fewer cards than requested" in {
      val testCards = List(Card(Rank.Ace, Suit.Spades))
      val cardGenerator: (Int, List[Card]) => List[Card] = (amount, cards) => cards.take(amount)

      val player = getNewPlayer("Charlie", 3, testCards, cardGenerator)

      player.name shouldEqual "Charlie"
      player.cards shouldEqual List(Card(Rank.Ace, Suit.Spades))
      player.turn shouldEqual Turn.Watching
    }
  }
}