package round

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TurnSpec extends AnyWordSpec with Matchers {

  "A Turn" should {

    "have all the correct enum values" in {
      // Test that the enum contains all expected values
      Turn.values should contain theSameElementsAs Seq(
        Turn.Defending,
        Turn.FirstlyAttacking,
        Turn.SecondlyAttacking,
        Turn.Watching,
        Turn.Finished
      )
    }

    "allow comparing turns" in {
      // Test comparing different turns
      Turn.Defending should not equal Turn.FirstlyAttacking
      Turn.Finished shouldBe Turn.Finished
    }

    "be pattern matched correctly" in {
      // Test pattern matching with the Turn enum
      def matchTurn(turn: Turn): String = turn match {
        case Turn.Defending         => "Player is defending"
        case Turn.FirstlyAttacking  => "Player is attacking first"
        case Turn.SecondlyAttacking => "Player is attacking second"
        case Turn.Watching          => "Player is watching"
        case Turn.Finished          => "Game is finished"
      }

      matchTurn(Turn.Defending) shouldBe "Player is defending"
      matchTurn(Turn.FirstlyAttacking) shouldBe "Player is attacking first"
      matchTurn(Turn.SecondlyAttacking) shouldBe "Player is attacking second"
      matchTurn(Turn.Watching) shouldBe "Player is watching"
      matchTurn(Turn.Finished) shouldBe "Game is finished"
    }
  }
}