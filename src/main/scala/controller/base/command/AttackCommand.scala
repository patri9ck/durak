package controller.base.command

import controller.base.BaseController
import model.{Card, StatusBuilder, Turn}

class AttackCommand(controller: BaseController, card: Card) extends MementoCommand(controller) {

  override def run(): Unit = {
    val attacking = controller.current.get

    val updated = attacking.copy(cards = attacking.cards.filterNot(_ == card))

    val statusBuilder = StatusBuilder(controller.status)
      .setPlayers(controller.updatePlayers(controller.status.players, attacking, updated))
      .setUndefended(card :: controller.status.undefended)
      .setDenied(false)

    if (updated.turn == Turn.FirstlyAttacking && controller.byTurn(Turn.SecondlyAttacking).isEmpty || updated.turn == Turn.SecondlyAttacking) {
      statusBuilder.setTurn(Turn.Defending)
    } else {
      statusBuilder.setTurn(Turn.SecondlyAttacking)
    }

    if (controller.hasFinished(updated, statusBuilder)) {
      controller.finish(updated, statusBuilder)
    }

    controller.status = statusBuilder.status
  }
}