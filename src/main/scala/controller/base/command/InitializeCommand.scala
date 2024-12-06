package controller.base.command

import controller.base.BaseController
import model.Turn

class InitializeCommand(controller: BaseController, amount: Int, names: List[String]) extends MementoCommand(controller) {
  
  override def run(): Unit = {
    require(controller.status.turn == Turn.Uninitialized)

    controller.status = controller.status.initialize(amount, names)
  }
}
