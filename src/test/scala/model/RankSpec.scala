package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RankSpec extends AnyWordSpec with Matchers {

  "Rank" should {
    "getBiggestRankLength" should {
      "return the biggest rank display length" in {
        Rank.getBiggestRankLength should be(2)
      }
    }
  }
}