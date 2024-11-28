package controller.base.command

import controller.base.BaseController
import model.{Card, StatusBuilder, Turn}

class DefendCommand(controller: BaseController, used: Card, undefended: Card) extends MementoCommand(controller) {

  override def run(): Unit = {
    val defending = controller.byTurn(Turn.Defending).get

    val updated = defending.copy(cards = defending.cards.filterNot(_ == used))

    val statusBuilder = StatusBuilder(controller.status)
      .setPlayers(controller.updatePlayers(controller.status.players, defending, updated))
      .setUndefended(controller.status.undefended.filterNot(_ == undefended))
      .setDefended(undefended :: controller.status.defended)
      .setUsed(used :: controller.status.used)
      .setTurn(Turn.FirstlyAttacking)

    if (controller.hasFinished(updated, statusBuilder)) {
      controller.finish(updated, statusBuilder)
    }

    controller.status = statusBuilder.status
  }
}
