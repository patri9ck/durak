package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{Format, JsNumber, Json}

class RankSpec extends AnyWordSpec with Matchers {

  "Rank" should {
    "getBiggestRankLength" should {
      "return the biggest rank display length" in {
        Rank.getBiggestRankLength should be(2)
      }
    }

    "Format[Rank]" should {
      "convert a Rank into JSON and get a Rank from JSON" in {
        val json = """"Ace""""

        val rank = Rank.Ace

        implicitly[Format[Rank]].writes(rank) should be(Json.parse(json))
        implicitly[Format[Rank]].reads(Json.parse(json)).get should be(rank)
      }

      "return a JsError when an unknown Rank is passed" in {
        implicitly[Format[Rank]].reads(Json.parse(""""Unknown"""")).isError should be(true)
      }

      "return a JsError when there is no JSON string" in {
        implicitly[Format[Rank]].reads(JsNumber(0)).isError should be(true)
      }
    }
  }
}