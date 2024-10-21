package round

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import card._

class PlayerSpec extends AnyWordSpec with Matchers {

  "getNewPlayer" should {

    "create a player with the correct name and cards" in {
      // Dummy Card, Rank und Suit Implementierungen
      val cardGenerator: Int => List[Card] = cardAmount => List(
        Card(Rank.Ace, Suit.Hearts),
        Card(Rank.Two, Suit.Diamonds)
      )

      val playerName = "Alice"
      val cardAmount = 2

      // Erstelle den neuen Spieler
      val newPlayer = getNewPlayer(playerName, cardAmount, cardGenerator)

      // Überprüfe den Namen
      newPlayer.name shouldBe playerName

      // Überprüfe, dass die Anzahl der Karten stimmt
      newPlayer.cards should have length cardAmount

      // Überprüfe, dass die Karten korrekt generiert wurden
      newPlayer.cards shouldBe List(
        Card(Rank.Ace, Suit.Hearts),
        Card(Rank.Two, Suit.Diamonds)
      )
    }

    "create a player with an empty card list if no cards are generated" in {
      // Generator für leere Kartenliste
      val cardGenerator: Int => List[Card] = _ => List.empty

      val playerName = "Bob"
      val cardAmount = 0

      // Erstelle den neuen Spieler
      val newPlayer = getNewPlayer(playerName, cardAmount, cardGenerator)

      // Überprüfe den Namen
      newPlayer.name shouldBe playerName

      // Überprüfe, dass die Kartenliste leer ist
      newPlayer.cards shouldBe empty
    }

    "create a player with the correct number of generated cards" in {
      // Generator für eine Liste von 5 Karten
      val cardGenerator: Int => List[Card] = cardAmount =>
        List.fill(cardAmount)(Card(Rank.Three, Suit.Clubs))

      val playerName = "Charlie"
      val cardAmount = 5

      // Erstelle den neuen Spieler
      val newPlayer = getNewPlayer(playerName, cardAmount, cardGenerator)

      // Überprüfe den Namen
      newPlayer.name shouldBe playerName

      // Überprüfe die Anzahl der Karten
      newPlayer.cards should have length cardAmount

      // Überprüfe, dass die Karten korrekt generiert wurden
      newPlayer.cards shouldBe List.fill(cardAmount)(Card(Rank.Three, Suit.Clubs))
    }
  }
}