import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class RankSpec extends AnyWordSpec with Matchers {

  "A Rank" should {

    "have the correct order and display values" in {
      Rank.Two.order should be(2)
      Rank.Two.display should be("2")
      Rank.Ace.order should be(14)
      Rank.Ace.display should be("A")
    }

    "return the biggest rank display length" in {
      Rank.getBiggestRankLength should be(2)
    }
  }
}