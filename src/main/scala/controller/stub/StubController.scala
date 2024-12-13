package controller.stub

import controller.Controller
import model.status.Status
import model.{Card, Player, Turn}

class StubController(val status: Status = new Status) extends Controller {

  override def initialize(amount: Int, names: List[String]): Unit = {}

  override def initialize(amount: Int, names: List[String], attacking: String): Unit = {}

  override def deny(): Unit = {}

  override def pickUp(): Unit = {}

  override def attack(card: Card): Unit = {}

  override def canAttack(card: Card): Boolean = true

  override def defend(used: Card, undefended: Card): Unit = {}

  override def canDefend(used: Card, undefended: Card): Boolean = true

  override def byTurn(turn: Turn): Option[Player] = None

  override def current: Option[Player] = None

  override def undo(): Unit = {}

  override def redo(): Unit = {}

}
