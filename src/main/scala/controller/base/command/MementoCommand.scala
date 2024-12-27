package controller.base.command

import com.google.inject.Inject
import controller.Controller
import controller.base.BaseController
import model.status.Status
import util.Command

trait MementoCommand(val controller: BaseController) extends Command {

  private var memento: Status = controller.status
  
  def execute(): Unit
  
  override def doStep(): Unit = {
    memento = controller.status

    execute()
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
