package view

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ViewCreatorSpec extends AnyWordSpec with Matchers {

  "ViewCreator" should {
    "apply(ViewType)" should {
      "return a Tui instance" in {
        ViewCreator(ViewType.Tui) should be(Tui)
      }

      "return a Gui instance" in {
        ViewCreator(ViewType.Gui) should be(null)
      }
    }
  }
}
