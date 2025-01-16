package controller.base.command

import com.google.inject.Guice
import controller.base.BaseController
import model.status.StatusBuilder
import model.{Card, Turn}
import module.DurakModule

class DefendCommand(controller: BaseController, used: Card, undefended: Card) extends MementoCommand(controller) {

  private val injector = Guice.createInjector(DurakModule())

  override def execute(): Unit = {
    val defending = controller.byTurn(Turn.Defending).get

    val updated = defending.copy(cards = defending.cards.filterNot(_ == used))

    var statusBuilder = injector.getInstance(classOf[StatusBuilder])
      .setStatus(controller.status)
      .setPlayers(controller.updatePlayers(controller.status.players, defending, updated))
      .setUndefended(controller.status.undefended.filterNot(_ == undefended))
      .setDefended(undefended :: controller.status.defended)
      .setUsed(used :: controller.status.used)
      .setTurn(Turn.FirstlyAttacking)

    if (controller.hasFinished(updated, statusBuilder)) {
      statusBuilder = controller.finish(updated, statusBuilder)
    }

    controller.status = statusBuilder.status
  }
}
