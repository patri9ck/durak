package controller.base.command

import controller.base.BaseController

class StartCommand(controller: BaseController, amount: Int, names: List[String]) extends MementoCommand(controller) {
  
  override def doStep(): Unit = {
    memento = controller.status
    
    controller.status = controller.status.initialize(amount, names)
  }
}
