package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{Format, JsNumber, Json}

class TurnSpec extends AnyWordSpec with Matchers {

  "Turn" should {
    "Format[Turn]" should {
      "convert a Turn into JSON and get a Turn from JSON" in {
        val json = """"Watching""""

        val turn = Turn.Watching

        implicitly[Format[Turn]].writes(turn) should be(Json.parse(json))
        implicitly[Format[Turn]].reads(Json.parse(json)).get should be(Turn.Watching)
      }

      "return a JsError when an unknown Turn is passed" in {
        implicitly[Format[Turn]].reads(Json.parse(""""Unknown"""")).isError should be(true)
      }

      "return a JsError when there is no JSON string" in {
        implicitly[Format[Turn]].reads(JsNumber(0)).isError should be(true)
      }
    }
  }
}
