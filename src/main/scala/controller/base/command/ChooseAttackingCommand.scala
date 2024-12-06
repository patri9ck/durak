package controller.base.command

import controller.base.BaseController
import model.{Player, StatusBuilder, Turn}

class ChooseAttackingCommand(controller: BaseController, attacking: Player) extends MementoCommand(controller)  {

  override def run(): Unit = {
    require(controller.status.turn == Turn.Initialized)
    require(controller.status.players.contains(attacking))
    
    controller.status = StatusBuilder(controller.status)
      .setPlayers(controller.chooseAttacking(controller.status.players, controller.status.players.indexWhere(_ == attacking)))
      .setTurn(Turn.FirstlyAttacking)
      .status
  }

}
