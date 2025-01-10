package controller.base.command

import com.google.inject.Guice
import controller.base.BaseController
import model.Turn
import model.status.StatusBuilder
import module.DurakModule

class PickUpCommand(controller: BaseController) extends MementoCommand(controller) {

  private val injector = Guice.createInjector(DurakModule())

  override def execute(): Unit = {
    val defending = controller.current.get

    val statusBuilder = injector.getInstance(classOf[StatusBuilder])
      .setStatus(controller.status)

    val updated = defending.copy(cards = defending.cards ++ controller.status.used ++ controller.status.defended ++ controller.status.undefended)

    statusBuilder.setPlayers(controller.updatePlayers(statusBuilder.getPlayers, defending, updated))

    controller.drawFromStack(statusBuilder)

    controller.status = statusBuilder
      .setPlayers(controller.chooseNextAttacking(statusBuilder.getPlayers, updated))
      .resetRound
      .status
  }
}
