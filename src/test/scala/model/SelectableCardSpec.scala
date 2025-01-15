package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SelectableCardSpec extends AnyWordSpec with Matchers {

  "SelectableCard" should {
    "this(Card, Boolean)" should {
      "return a new SelectableCard with card and selected set to false by default" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val selectableCard = SelectableCard(card)

        selectableCard.card should be(card)
        selectableCard.selected should be(false)
      }
    }
  }

}
