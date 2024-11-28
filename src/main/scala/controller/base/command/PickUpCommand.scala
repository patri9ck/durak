package controller.base.command

import controller.base.BaseController
import model.{Status, StatusBuilder, Turn}
import util.Command

class PickUpCommand(controller: BaseController) extends Command {

  private var memento: Status = controller.status

  override def doStep(): Unit = {
    memento = controller.status

    val defending = controller.getPlayer.get

    val statusBuilder = StatusBuilder.create(controller.status)

    controller.drawFromStack(statusBuilder)

    val updated = defending.copy(cards = defending.cards ++ controller.status.used ++ controller.status.defended ++ controller.status.undefended)

    controller.status = statusBuilder
      .setPlayers(controller.chooseNextAttacking(controller.updatePlayers(controller.status.players, defending, updated), updated))
      .resetRound
      .status
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
