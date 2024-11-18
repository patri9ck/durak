import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class GroupSpec extends AnyWordSpec with Matchers {

  "A Group" should {

    "correctly initialize with given players, stack, trump card, and amount" in {
      val players = List(Player("Player1", List(), Turn.Watching), Player("Player2", List(), Turn.Watching))
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
      val trump = Card(Rank.Queen, Suit.Diamonds)
      val amount = 6

      val group = Group(players, stack, trump, amount)

      group.players should be(players)
      group.stack should be(stack)
      group.trump should be(trump)
      group.amount should be(amount)
    }

    "allow updating the players list" in {
      var players = List(Player("Player1", List(), Turn.Watching), Player("Player2", List(), Turn.Watching))
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
      val trump = Card(Rank.Queen, Suit.Diamonds)
      val amount = 6

      val group = Group(players, stack, trump, amount)
      val newPlayers = List(Player("Player3", List(), Turn.Watching))

      val updatedGroup = group.copy(players = newPlayers)

      updatedGroup.players should be(newPlayers)
      updatedGroup.stack should be(stack)
      updatedGroup.trump should be(trump)
      updatedGroup.amount should be(amount)
    }

    "allow updating the stack" in {
      val players = List(Player("Player1", List(), Turn.Watching), Player("Player2", List(), Turn.Watching))
      var stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
      val trump = Card(Rank.Queen, Suit.Diamonds)
      val amount = 6

      val group = Group(players, stack, trump, amount)
      val newStack = List(Card(Rank.Two, Suit.Clubs))

      val updatedGroup = group.copy(stack = newStack)

      updatedGroup.players should be(players)
      updatedGroup.stack should be(newStack)
      updatedGroup.trump should be(trump)
      updatedGroup.amount should be(amount)
    }

    "allow updating the trump card" in {
      val players = List(Player("Player1", List(), Turn.Watching), Player("Player2", List(), Turn.Watching))
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
      var trump = Card(Rank.Queen, Suit.Diamonds)
      val amount = 6

      val group = Group(players, stack, trump, amount)
      val newTrump = Card(Rank.Jack, Suit.Clubs)

      val updatedGroup = group.copy(trump = newTrump)

      updatedGroup.players should be(players)
      updatedGroup.stack should be(stack)
      updatedGroup.trump should be(newTrump)
      updatedGroup.amount should be(amount)
    }

    "allow updating the amount" in {
      val players = List(Player("Player1", List(), Turn.Watching), Player("Player2", List(), Turn.Watching))
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
      val trump = Card(Rank.Queen, Suit.Diamonds)
      var amount = 6

      val group = Group(players, stack, trump, amount)
      val newAmount = 8

      val updatedGroup = group.copy(amount = newAmount)

      updatedGroup.players should be(players)
      updatedGroup.stack should be(stack)
      updatedGroup.trump should be(trump)
      updatedGroup.amount should be(newAmount)
    }
  }
}