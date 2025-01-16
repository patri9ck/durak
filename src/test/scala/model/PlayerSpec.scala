package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import scala.xml.Utility

class PlayerSpec extends AnyWordSpec with Matchers {

  "Player" should {
    "toString" should {
      "return the player's name" in {
        val player = Player("Player 1", List(), Turn.Watching)

        player.toString should be("Player 1")
      }
    }

    "toXml" should {
      "convert a Player into XML" in {
        val xml =
          <player>
            <name>Player1</name>
            <cards>
              <card>
                <rank>Ace</rank>
                <suit>Spades</suit>
              </card>
            </cards>
            <turn>Watching</turn>
          </player>

        Utility.trim(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Watching).toXml).toString should be(Utility.trim(xml).toString)
      }
    }

    "playerFormat" should {
      "convert a Card into JSON and get a Card from JSON" in {
        val json = """{"name":"Player1","cards":[{"rank":"Ace","suit":"Spades"}],"turn":"Watching"}"""

        val player = Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Watching)

        Player.playerFormat.writes(player) should be(Json.parse(json))
        Player.playerFormat.reads(Json.parse(json)).get should be(player)
      }
    }

    "fromXml(Node)" should {
      "get a Player from XML" in {
        val xml =
          <player>
            <name>Player1</name>
            <cards>
              <card>
                <rank>Ace</rank>
                <suit>Spades</suit>
              </card>
            </cards>
            <turn>Watching</turn>
          </player>

        Player.fromXml(xml) should be( Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Watching))
      }
    }
  }
}
