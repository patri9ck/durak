package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SuitSpec extends AnyWordSpec with Matchers {
  
  "Suit" should {
    "toString" should {
      "return the suit's display" in {
        Suit.Diamonds.toString should be(Suit.Diamonds.display)
      }
    }
  }
}
