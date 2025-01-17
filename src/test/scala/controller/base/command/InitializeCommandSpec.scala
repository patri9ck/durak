package controller.base.command

import controller.base.BaseController
import model.Turn
import model.io.JsonFileIo
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class InitializeCommandSpec extends AnyWordSpec with Matchers {

  "execute()" should {
    "set the players, stack, amount, trump and the turn to FirstlyAttacking" in {
      val controller = BaseController(JsonFileIo())

      InitializeCommand(controller, 6, List("Player1", "Player2"), "Player1").execute()

      controller.status.players.size should be(2)
      controller.status.stack.size should be(39)
      controller.status.amount should be(6)
      controller.status.trump should not be None
      controller.status.turn should be(Turn.FirstlyAttacking)
    }
  }
}
