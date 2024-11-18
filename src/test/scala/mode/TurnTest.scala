import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class TurnSpec extends AnyWordSpec with Matchers {

  "A Turn" should {

    "have the correct enum values" in {
      Turn.values should contain allOf (Turn.Defending, Turn.FirstlyAttacking, Turn.SecondlyAttacking, Turn.Watching, Turn.Finished)
    }

    "have the correct string representations" in {
      Turn.Defending.toString should be("Defending")
      Turn.FirstlyAttacking.toString should be("FirstlyAttacking")
      Turn.SecondlyAttacking.toString should be("SecondlyAttacking")
      Turn.Watching.toString should be("Watching")
      Turn.Finished.toString should be("Finished")
    }
  }
}