package controller.base.command

import controller.base.BaseController
import model.{StatusBuilder, Turn}

class DenyCommand(controller: BaseController) extends MementoCommand(controller) {

  override def doStep(): Unit = {
    memento = controller.status

    val attacking = controller.getPlayer.get

    val statusBuilder = StatusBuilder(controller.status)

    if (controller.status.denied || controller.byTurn(Turn.SecondlyAttacking).isEmpty) {
      if (controller.status.undefended.isEmpty) {
        controller.drawFromStack(statusBuilder)

        statusBuilder
          .setPlayers(controller.chooseNextAttacking(controller.status.players, controller.byTurn(Turn.FirstlyAttacking).get))
          .setTurn(Turn.FirstlyAttacking)
          .resetRound
      } else {
        statusBuilder
          .setTurn(Turn.Defending)
      }
    } else {
      statusBuilder
        .setTurn(Turn.SecondlyAttacking)
        .setDenied(true)
    }

    controller.status = statusBuilder.status
  }
}
