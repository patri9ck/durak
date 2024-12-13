package controller.base.command

import controller.base.BaseController
import model.{Card, Player, Turn}
import model.status.MutableStatusBuilder

import scala.util.Random

class InitializeCommand(controller: BaseController, amount: Int, names: List[String], attacking: String) extends MementoCommand(controller) {
  
  override def execute(): Unit = {
    val deck = Card.getDeck

    val index = Random.nextInt(deck.size)
    val trump = deck(index)

    var remaining = deck.patch(index, Nil, 1)

    val players = names.map { name =>
      val playerCards = remaining.take(amount)
      remaining = remaining.drop(amount)
      Player(name, playerCards, Turn.Watching)
    }

    controller.status = MutableStatusBuilder(controller.status)
      .setPlayers(controller.chooseAttacking(players, players.indexWhere(_.name == attacking)))
      .setStack(remaining)
      .setAmount(amount)
      .setTrump(trump)
      .setTurn(Turn.FirstlyAttacking)
      .status
  }
}
