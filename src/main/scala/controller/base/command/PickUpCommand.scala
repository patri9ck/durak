package controller.base.command

import controller.base.BaseController
import model.{StatusBuilder, Turn}

class PickUpCommand(controller: BaseController) extends MementoCommand(controller) {

  override def doStep(): Unit = {
    memento = controller.status

    val defending = controller.getPlayer.get

    val statusBuilder = StatusBuilder(controller.status)

    controller.drawFromStack(statusBuilder)

    val updated = defending.copy(cards = defending.cards ++ controller.status.used ++ controller.status.defended ++ controller.status.undefended)

    controller.status = statusBuilder
      .setPlayers(controller.chooseNextAttacking(controller.updatePlayers(controller.status.players, defending, updated), updated))
      .resetRound
      .status
  }
}
