package controller.base.command

import controller.base.BaseController
import model.status.{MutableStatusBuilder, StatusBuilder}
import model.{Player, Turn}

class ChooseAttackingCommand(controller: BaseController, attacking: Player) extends MementoCommand(controller)  {

  override def execute(): Unit = {
    controller.status = MutableStatusBuilder(controller.status)
      .setPlayers(controller.chooseAttacking(controller.status.players, controller.status.players.indexWhere(_ == attacking)))
      .setTurn(Turn.FirstlyAttacking)
      .status
  }

}
