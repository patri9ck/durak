package card

import card.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RankSpec extends AnyWordSpec with Matchers {

  "A Rank" should {

    "have the correct display values for each rank" in {
      Rank.Two.display shouldBe "2"
      Rank.Three.display shouldBe "3"
      Rank.Four.display shouldBe "4"
      Rank.Five.display shouldBe "5"
      Rank.Six.display shouldBe "6"
      Rank.Seven.display shouldBe "7"
      Rank.Eight.display shouldBe "8"
      Rank.Nine.display shouldBe "9"
      Rank.Ten.display shouldBe "10"
      Rank.Jack.display shouldBe "J"
      Rank.Queen.display shouldBe "Q"
      Rank.King.display shouldBe "K"
      Rank.Ace.display shouldBe "A"
    }

    "have the correct order values for each rank" in {
      Rank.Two.order shouldBe 2
      Rank.Three.order shouldBe 3
      Rank.Four.order shouldBe 4
      Rank.Five.order shouldBe 5
      Rank.Six.order shouldBe 6
      Rank.Seven.order shouldBe 7
      Rank.Eight.order shouldBe 8
      Rank.Nine.order shouldBe 9
      Rank.Ten.order shouldBe 10
      Rank.Jack.order shouldBe 11
      Rank.Queen.order shouldBe 12
      Rank.King.order shouldBe 13
      Rank.Ace.order shouldBe 14
    }

    "return the correct biggest rank length with getBiggestRankLength" in {
      getBiggestRankLength shouldBe 2
    }

    "generate a random rank with getRandomRank" in {
      val randomRank = getRandomRank
      Rank.values should contain(randomRank)
    }
  }
}