package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TurnSpec extends AnyWordSpec with Matchers {
  
  "Turn" should {
    "toString" should {
      "return the turn's name" in {
        Turn.Watching.toString should be(Turn.Watching.name)
      }
    }
  }

}
