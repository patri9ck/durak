package controller.base.command

import controller.base.BaseController

class InitializeCommand(controller: BaseController, amount: Int, names: List[String]) extends MementoCommand(controller) {
  
  override def execute(): Unit = {
    controller.status = controller.status.initialize(amount, names)
  }
}
