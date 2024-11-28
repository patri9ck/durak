package controller.base.command

import controller.base.BaseController
import model.{Card, Status, StatusBuilder, Turn}
import util.Command

class AttackCommand(controller: BaseController, card: Card) extends Command {

  private var memento: Status = controller.status

  override def doStep(): Unit = {
    require(controller.status.turn == Turn.FirstlyAttacking || controller.status.turn == Turn.SecondlyAttacking)
    
    memento = controller.status

    val attacking = controller.getPlayer.get

    val updated = attacking.copy(cards = attacking.cards.filterNot(_ == card))

    val statusBuilder = StatusBuilder.create(controller.status)
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