import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {

    "correctly initialize with given name, cards, and turn" in {
      val cards = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
      val player = Player("Player1", cards, Turn.Watching)

      player.name should be("Player1")
      player.cards should be(cards)
      player.turn should be(Turn.Watching)
    }

    "allow updating the name" in {
      val player = Player("Player1", List(), Turn.Watching)
      val updatedPlayer = player.copy(name = "Player2")

      updatedPlayer.name should be("Player2")
      updatedPlayer.cards should be(player.cards)
      updatedPlayer.turn should be(player.turn)
    }

    "allow updating the cards" in {
      val player = Player("Player1", List(), Turn.Watching)
      val newCards = List(Card(Rank.Ace, Suit.Spades))
      val updatedPlayer = player.copy(cards = newCards)

      updatedPlayer.name should be(player.name)
      updatedPlayer.cards should be(newCards)
      updatedPlayer.turn should be(player.turn)
    }

    "allow updating the turn" in {
      val player = Player("Player1", List(), Turn.Watching)
      val updatedPlayer = player.copy(turn = Turn.Defending)

      updatedPlayer.name should be(player.name)
      updatedPlayer.cards should be(player.cards)
      updatedPlayer.turn should be(Turn.Defending)
    }
  }
}