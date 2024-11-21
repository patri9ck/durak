package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RankSpec extends AnyWordSpec with Matchers {

  "Rank" should {
    "toString" should {
      "return the rank's display" in {
        Rank.Ace.toString should be(Rank.Ace.display)
      }
    }
    
    "getBiggestRankLength" should {
      "return the biggest rank display length" in {
        Rank.getBiggestRankLength should be(2)
      }
    }
  }
}