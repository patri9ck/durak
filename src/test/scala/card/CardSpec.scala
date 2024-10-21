package card

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {

    "be generated randomly with getRandomCard" in {
      val card = getRandomCard
      card.rank shouldBe a [Rank]
      card.suit shouldBe a [Suit]
    }

    "return the specified number of cards with getRandomCards" in {
      val amount = 5
      val cards = getRandomCards(amount)
      cards.length shouldBe amount
    }
  }
}
