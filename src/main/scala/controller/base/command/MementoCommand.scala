package controller.base.command

import controller.base.BaseController
import model.Status
import util.Command

trait MementoCommand(val controller: BaseController) extends Command {

  private var memento: Status = controller.status
  
  def run(): Unit
  
  override def doStep(): Unit = {
    memento = controller.status

    run()
  }

  override def undoStep(): Unit = {
    val memento = controller.status

    controller.status = this.memento

    this.memento = memento
  }

  override def redoStep(): Unit = {
    val memento = controller.status

    controller.status = this.memento

    this.memento = memento
  }
}
