package round

import card.*
import controller.{Group, createGroup}
import model.{Card, Player, Rank, Suit, Turn}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GroupSpec extends AnyWordSpec with Matchers {

  "chooseDefending" should {
    "return the same group if players.isEmpty" in {
      val group = Group(List.empty[Player])

      val updatedGroup = group.chooseDefending(Player("Unknown", List.empty, Turn.Watching))

      updatedGroup shouldEqual group // Die Gruppe bleibt unverändert
    }

    "set the defending player and adjust the turns correctly for more than 2 players" in {
      val players = List(
        Player("Alice", List(Card(Rank.Ace, Suit.Hearts)), Turn.Watching),
        Player("Bob", List(Card(Rank.Two, Suit.Diamonds)), Turn.Watching),
        Player("Charlie", List(Card(Rank.Three, Suit.Clubs)), Turn.Watching),
        Player("Alice", List(Card(Rank.Five, Suit.Spades)), Turn.Defending)
      )
      val group = Group(players)

      val updatedGroup = group.chooseDefending(players(1))

      updatedGroup.players(0).turn shouldEqual Turn.SecondlyAttacking
      updatedGroup.players(1).turn shouldEqual Turn.Defending
      updatedGroup.players(2).turn shouldEqual Turn.FirstlyAttacking

      updatedGroup.players.foreach { player =>
        if (player.name != "Bob" && player.name != "Alice" && player.name != "Charlie") {
          player.turn shouldEqual Turn.Watching
        }
      }
    }

    "set the defending player and adjust the turns correctly for 2 players" in {
      val players = List(
        Player("Alice", List(Card(Rank.Ace, Suit.Hearts)), Turn.Watching),
        Player("Bob", List(Card(Rank.Two, Suit.Diamonds)), Turn.Watching)
      )
      val group = Group(players)

      val updatedGroup = group.chooseDefending(players(1)) // Bob is defending

      updatedGroup.players(0).turn shouldEqual Turn.FirstlyAttacking // Alice
      updatedGroup.players(1).turn shouldEqual Turn.Defending // Bob
    }

    "return the same group if the defending player is not found" in {
      val players = List(
        Player("Alice", List(Card(Rank.Ace, Suit.Hearts)), Turn.Watching),
        Player("Bob", List(Card(Rank.Two, Suit.Diamonds)), Turn.Watching)
      )
      val group = Group(players)

      val unknownPlayer = Player("Unknown", List(Card(Rank.Ace, Suit.Hearts)), Turn.Watching)
      val updatedGroup = group.chooseDefending(unknownPlayer)

      updatedGroup shouldEqual group
    }
  }

  "createGroup" should {
    "create a group with the correct number of players and cards" in {
      val names = List("Alice", "Bob", "Charlie")
      val group = createGroup(2, names)

      group.players should have length 3
      group.players.foreach { player =>
        player.cards should have length 2
      }
    }
  }

  "chooseDefendingRandomly" should {
    "set a random player as defending" in {
      val players = List(
        Player("Alice", List(Card(Rank.Ace, Suit.Hearts)), Turn.Watching),
        Player("Bob", List(Card(Rank.Two, Suit.Diamonds)), Turn.Watching),
        Player("Charlie", List(Card(Rank.Three, Suit.Clubs)), Turn.Watching)
      )
      val group = Group(players)

      val updatedGroup = group.chooseDefendingRandomly()

      updatedGroup.players.count(_.turn == Turn.Defending) shouldEqual 1
    }
  }
}