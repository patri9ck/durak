package round

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import card._ // Importiere die benötigten Klassen aus dem card-Package

class RoundSpec extends AnyWordSpec with Matchers {

  "A Round" should {

    "return the correct loser when stop condition is met" in {
      // Erstelle Spieler mit echten Karten
      val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Hearts)))
      val player2 = Player("Bob", List(Card(Rank.Two, Suit.Diamonds)))
      val player3 = Player("Charlie", List(Card(Rank.Three, Suit.Clubs)))

      // Erstelle die Gruppe mit einer Map von Spielern und deren Zügen
      val initialGroup = Group(Map(
        player1 -> Turn.FirstlyAttacking,
        player2 -> Turn.Watching,
        player3 -> Turn.Defending
      ))

      // Simuliere das Durchführen einer Runde, indem ein Spieler pro Runde entfernt wird
      val run: Group => Group = group => {
        // Entferne den ersten Spieler aus der Map (z.B. Alice)
        if (group.mappedPlayers.size > 1) {
          val updatedMap = group.mappedPlayers - group.mappedPlayers.keys.head // Entferne den ersten Spieler
          Group(updatedMap)
        } else {
          group // Wenn nur noch ein Spieler übrig ist, gib die Gruppe unverändert zurück
        }
      }

      // Stop-Bedingung: Stoppe, wenn nur noch ein Spieler übrig ist
      val stop: Group => Boolean = group => group.mappedPlayers.size == 1

      // Verlierer ist der letzte verbleibende Spieler
      val loser: Group => Player = group => group.mappedPlayers.keys.head

      // Erstelle eine neue Runde
      val round = new Round(initialGroup, run, stop, loser)

      // Starte die Runde und überprüfe den Verlierer
      val result = round.start()
      result shouldBe player3 // Charlie sollte der Verlierer sein
    }

    "immediately return the loser if stop condition is already met" in {
      // Starte mit einer Gruppe, die nur einen Spieler hat
      val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Hearts)))

      // Erstelle die Gruppe mit nur einem Spieler
      val initialGroup = Group(Map(player1 -> Turn.FirstlyAttacking))

      // Simuliere keine Veränderung in der Gruppe
      val run: Group => Group = group => group

      // Stop-Bedingung: Bereits erfüllt, wenn nur ein Spieler vorhanden ist
      val stop: Group => Boolean = group => group.mappedPlayers.size == 1

      // Verlierer ist der einzige Spieler
      val loser: Group => Player = group => group.mappedPlayers.keys.head

      // Erstelle eine neue Runde
      val round = new Round(initialGroup, run, stop, loser)

      // Starte die Runde und überprüfe den Verlierer
      val result = round.start()
      result shouldBe player1 // Alice sollte der Verlierer sein
    }
  }
}