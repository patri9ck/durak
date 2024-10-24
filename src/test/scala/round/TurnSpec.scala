package round

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TurnSpec extends AnyWordSpec with Matchers {

  "A Turn" should {

    "have all the correct enum values" in {
      Turn.values should contain theSameElementsAs Seq(
        Turn.Defending,
        Turn.FirstlyAttacking,
        Turn.SecondlyAttacking,
        Turn.Watching,
        Turn.Finished
      )
    }

    "allow comparing turns" in {
      Turn.Defending should not equal Turn.FirstlyAttacking
      Turn.Finished shouldBe Turn.Finished
    }
  }
}