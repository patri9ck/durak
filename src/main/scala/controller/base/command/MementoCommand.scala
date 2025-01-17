package controller.base.command

import controller.base.BaseController
import model.status.Status
import util.Command

/**
 * Implements a [[util.Command]] that uses the memento pattern by getting and setting the status using [[controller.Controller.status]].
 */
trait MementoCommand(val controller: BaseController) extends Command {

  /**
   * The current status used for undo and redo. Not private to allow for easier testing.
   */
  var memento: Status = controller.status

  /**
   * Executes the command, containing business logic.
   */
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
