package model.status

import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import scala.xml.Utility

class StatusSpec extends AnyWordSpec with Matchers {

  "Status" should {
    "toXml" should {
      "convert a Status into XML and use the values of Options if they are Some" in {
        val xml =
          <status>
            <players>
              <player>
                <name>Player1</name>
                <cards>
                  <card>
                    <rank>Ace</rank>
                    <suit>Spades</suit>
                  </card>
                </cards>
                <turn>Finished</turn>
              </player>
            </players>
            <stack>
              <card>
                <rank>King</rank>
                <suit>Hearts</suit>
              </card>
            </stack>
            <trump>
              <card>
                <rank>Queen</rank>
                <suit>Diamonds</suit>
              </card>
            </trump>
            <amount>5</amount>
            <turn>Finished</turn>
            <defended>
              <card>
                <rank>Ten</rank>
                <suit>Clubs</suit>
              </card>
            </defended>
            <undefended>
              <card>
                <rank>Nine</rank>
                <suit>Spades</suit>
              </card>
            </undefended>
            <used>
              <card>
                <rank>Eight</rank>
                <suit>Hearts</suit>
              </card>
            </used>
            <denied>true</denied>
            <passed>
              <player>
                <name>Player2</name>
                <cards>
                  <card>
                    <rank>Seven</rank>
                    <suit>Diamonds</suit>
                  </card>
                </cards>
                <turn>Watching</turn>
              </player>
            </passed>
          </status>

        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))

        Utility.trim(status.toXml).toString should be(Utility.trim(xml).toString)
      }

      "convert a Status into XML and use Nil if the values of Options are None" in {
        val xml =
          <status>
            <players/>
            <stack/>
            <trump/>
            <amount>0</amount>
            <turn>Uninitialized</turn>
            <defended/>
            <undefended/>
            <used/>
            <denied>false</denied>
            <passed/>
          </status>

        val status = Status()

        Utility.trim(status.toXml).toString should be(Utility.trim(xml).toString)
      }
    }

    "fromXml(Elem)" should {
      "get a Status from XML" in {
        val xml =
          <status>
            <players>
              <player>
                <name>Player1</name>
                <cards>
                  <card>
                    <rank>Ace</rank>
                    <suit>Spades</suit>
                  </card>
                </cards>
                <turn>Finished</turn>
              </player>
            </players>
            <stack>
              <card>
                <rank>King</rank>
                <suit>Hearts</suit>
              </card>
            </stack>
            <trump>
              <card>
                <rank>Queen</rank>
                <suit>Diamonds</suit>
              </card>
            </trump>
            <amount>5</amount>
            <turn>Finished</turn>
            <defended>
              <card>
                <rank>Ten</rank>
                <suit>Clubs</suit>
              </card>
            </defended>
            <undefended>
              <card>
                <rank>Nine</rank>
                <suit>Spades</suit>
              </card>
            </undefended>
            <used>
              <card>
                <rank>Eight</rank>
                <suit>Hearts</suit>
              </card>
            </used>
            <denied>true</denied>
            <passed>
              <player>
                <name>Player2</name>
                <cards>
                  <card>
                    <rank>Seven</rank>
                    <suit>Diamonds</suit>
                  </card>
                </cards>
                <turn>Watching</turn>
              </player>
            </passed>
          </status>

        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))

        Status.fromXml(xml) should be(status)
      }
    }

    "statusFormat" should {
      "convert a Status into JSON and get a Status from JSON" in {
        val json = """{"used":[{"rank":"Eight","suit":"Hearts"}],"trump":{"rank":"Queen","suit":"Diamonds"},"players":[{"name":"Player1","cards":[{"rank":"Ace","suit":"Spades"}],"turn":"Finished"}],"passed":{"name":"Player2","cards":[{"rank":"Seven","suit":"Diamonds"}],"turn":"Watching"},"amount":5,"stack":[{"rank":"King","suit":"Hearts"}],"denied":true,"turn":"Finished","undefended":[{"rank":"Nine","suit":"Spades"}],"defended":[{"rank":"Ten","suit":"Clubs"}]}"""

        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))

        Status.statusFormat.writes(status) should be(Json.parse(json))
        Status.statusFormat.reads(Json.parse(json)).get should be(status)
      }
    }
  }
}
