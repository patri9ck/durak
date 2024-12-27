package controller.base.command

import com.google.inject.Guice
import controller.base.BaseController
import model.status.StatusBuilder
import model.{Card, Turn}
import module.DurakModule

class AttackCommand(controller: BaseController, card: Card) extends MementoCommand(controller) {

  private val injector = Guice.createInjector(DurakModule())

  override def execute(): Unit = {
    val attacking = controller.current.get

    val updated = attacking.copy(cards = attacking.cards.filterNot(_ == card))

    val statusBuilder = injector.getInstance(classOf[StatusBuilder])
      .setStatus(controller.status)
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