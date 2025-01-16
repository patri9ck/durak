package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{Format, JsNumber, Json}

class SuitSpec extends AnyWordSpec with Matchers {

  "Suit" should {
    "Format[Suit]" should {
      "convert a Suit into JSON and get a Suit from JSON" in {
        val json = """"Spades""""

        val suit = Suit.Spades

        implicitly[Format[Suit]].writes(suit) should be(Json.parse(json))
        implicitly[Format[Suit]].reads(Json.parse(json)).get should be(Suit.Spades)
      }

      "return a JsError when an unknown Suit is passed" in {
        implicitly[Format[Suit]].reads(Json.parse(""""Unknown"""")).isError should be(true)
      }

      "return a JsError when there is no JSON string" in {
        implicitly[Format[Suit]].reads(JsNumber(0)).isError should be(true)
      }
    }
  }
}
