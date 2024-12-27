package controller.base.command

import com.google.inject.Guice
import controller.base.BaseController
import model.Turn
import model.status.StatusBuilder
import module.DurakModule

class DenyCommand(controller: BaseController) extends MementoCommand(controller) {

  private val injector = Guice.createInjector(DurakModule())

  override def execute(): Unit = {
    val attacking = controller.current.get

    val statusBuilder = injector.getInstance(classOf[StatusBuilder])
      .setStatus(controller.status)

    if ((controller.status.denied || controller.byTurn(Turn.SecondlyAttacking).isEmpty) && controller.status.undefended.isEmpty) {
      if (controller.status.undefended.isEmpty) {
        controller.drawFromStack(statusBuilder)

        statusBuilder
          .setPlayers(controller.chooseNextAttacking(statusBuilder.getPlayers, statusBuilder.byTurn(Turn.FirstlyAttacking).get))
          .setTurn(Turn.FirstlyAttacking)
          .resetRound
      } else {
        statusBuilder
          .setTurn(Turn.Defending)
      }
    } else if (controller.status.turn == Turn.FirstlyAttacking) {
      statusBuilder
        .setTurn(Turn.SecondlyAttacking)
        .setDenied(true)
    } else {
      statusBuilder
        .setTurn(Turn.Defending)
    }

    controller.status = statusBuilder.status
  }
}
