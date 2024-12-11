package controller.base.command

import controller.base.BaseController
import model.Turn
import model.status.{MutableStatusBuilder, StatusBuilder}

class PickUpCommand(controller: BaseController) extends MementoCommand(controller) {

  override def execute(): Unit = {
    val defending = controller.current.get

    val statusBuilder = MutableStatusBuilder(controller.status)

    controller.drawFromStack(statusBuilder)

    val updated = defending.copy(cards = defending.cards ++ controller.status.used ++ controller.status.defended ++ controller.status.undefended)

    controller.status = statusBuilder
      .setPlayers(controller.chooseNextAttacking(controller.updatePlayers(controller.status.players, defending, updated), updated))
      .resetRound
      .status
  }
}
