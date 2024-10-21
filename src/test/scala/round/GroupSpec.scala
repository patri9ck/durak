package round

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import card._  // Importiere die benötigten Klassen aus dem card-Package

class GroupSpec extends AnyWordSpec with Matchers {

  "A Group" should {

    "correctly initialize with players and their turns" in {
      // Erstelle einige Spieler
      val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Hearts)))
      val player2 = Player("Bob", List(Card(Rank.Two, Suit.Diamonds)))
      val player3 = Player("Charlie", List(Card(Rank.Three, Suit.Clubs)))

      // Erstelle die Gruppe mit einer Map von Spielern und deren Zügen
      val group = Group(Map(
        player1 -> Turn.Defending,
        player2 -> Turn.FirstlyAttacking,
        player3 -> Turn.Watching
      ))

      // Überprüfe, dass die Gruppe die richtige Anzahl an Spielern enthält
      group.mappedPlayers.size shouldBe 3

      // Überprüfe, dass die Spieler die richtigen Züge haben
      group.mappedPlayers(player1) shouldBe Turn.Defending
      group.mappedPlayers(player2) shouldBe Turn.FirstlyAttacking
      group.mappedPlayers(player3) shouldBe Turn.Watching
    }

    "allow adding a new player" in {
      // Erstelle einige Spieler
      val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Hearts)))
      val player2 = Player("Bob", List(Card(Rank.Two, Suit.Diamonds)))

      // Erstelle die Gruppe mit einem Spieler
      var group = Group(Map(player1 -> Turn.Defending))

      // Überprüfe die anfängliche Gruppengröße
      group.mappedPlayers.size shouldBe 1

      // Füge einen neuen Spieler hinzu
      group = Group(group.mappedPlayers + (player2 -> Turn.FirstlyAttacking))

      // Überprüfe die neue Gruppengröße
      group.mappedPlayers.size shouldBe 2

      // Überprüfe, dass der neue Spieler hinzugefügt wurde
      group.mappedPlayers(player2) shouldBe Turn.FirstlyAttacking
    }

    "allow removing a player" in {
      // Erstelle einige Spieler
      val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Hearts)))
      val player2 = Player("Bob", List(Card(Rank.Two, Suit.Diamonds)))
      val player3 = Player("Charlie", List(Card(Rank.Three, Suit.Clubs)))

      // Erstelle die Gruppe mit drei Spielern
      var group = Group(Map(
        player1 -> Turn.Defending,
        player2 -> Turn.FirstlyAttacking,
        player3 -> Turn.Watching
      ))

      // Überprüfe die anfängliche Gruppengröße
      group.mappedPlayers.size shouldBe 3

      // Entferne einen Spieler (z.B. Alice)
      group = Group(group.mappedPlayers - player1)

      // Überprüfe die neue Gruppengröße
      group.mappedPlayers.size shouldBe 2

      // Überprüfe, dass der Spieler entfernt wurde
      group.mappedPlayers.get(player1) shouldBe None
      group.mappedPlayers.get(player2) shouldBe Some(Turn.FirstlyAttacking)
      group.mappedPlayers.get(player3) shouldBe Some(Turn.Watching)
    }

    "return the correct turn for a player" in {
      // Erstelle einige Spieler
      val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Hearts)))
      val player2 = Player("Bob", List(Card(Rank.Two, Suit.Diamonds)))

      // Erstelle die Gruppe mit zwei Spielern
      val group = Group(Map(
        player1 -> Turn.Defending,
        player2 -> Turn.FirstlyAttacking
      ))

      // Überprüfe, dass die Turns korrekt zurückgegeben werden
      group.mappedPlayers(player1) shouldBe Turn.Defending
      group.mappedPlayers(player2) shouldBe Turn.FirstlyAttacking
    }
  }
}