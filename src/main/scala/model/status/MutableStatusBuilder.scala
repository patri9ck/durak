package model.status

import model.status.Status
import model.{Card, Player, Turn}

class MutableStatusBuilder(private var players: List[Player] = Nil,
                           private var stack: List[Card] = Nil,
                           private var trump: Option[Card] = None,
                           private var amount: Int = 0,
                           private var turn: Turn = Turn.Watching,
                           private var defended: List[Card] = Nil,
                           private var undefended: List[Card] = Nil,
                           private var used: List[Card] = Nil,
                           private var denied: Boolean = false,
                           private var passed: Option[Player] = None) extends StatusBuilder {

  def this(status: Status) = this(status.players,
    status.stack,
    status.trump,
    status.amount,
    status.turn,
    status.defended,
    status.undefended,
    status.used,
    status.denied,
    status.passed)

  override def setPlayers(players: List[Player]): StatusBuilder = {
    this.players = players
  
    this
  }

  override def setStack(stack: List[Card]): StatusBuilder = {
    this.stack = stack

    this
  }

  override def setTrump(trump: Card): StatusBuilder = {
    this.trump = Some(trump)

    this
  }

  override def removeTrump(): StatusBuilder = {
    this.trump = None

    this
  }

  override def setAmount(amount: Int): StatusBuilder = {
    this.amount = amount

    this
  }

  override def setTurn(turn: Turn): StatusBuilder = {
    this.turn = turn

    this
  }

  override def setDefended(defended: List[Card]): StatusBuilder = {
    this.defended = defended

    this
  }

  override def setUndefended(undefended: List[Card]): StatusBuilder = {
    this.undefended = undefended

    this
  }

  override def setUsed(used: List[Card]): StatusBuilder = {
    this.used = used

    this
  }

  override def resetRound: StatusBuilder = {
    turn = Turn.FirstlyAttacking
    defended = List()
    undefended = List()
    used = List()
    denied = false

    this
  }

  override def setDenied(denied: Boolean): StatusBuilder = {
    this.denied = denied

    this
  }

  override def setPassed(passed: Player): StatusBuilder = {
    this.passed = Some(passed)

    this
  }

  override def removePassed(): StatusBuilder = {
    this.passed = None

    this
  }

  override def byTurn(turn: Turn): Option[Player] = {
    players.find(_.turn == turn)
  }

  def getPlayers: List[Player] = players

  def getStack: List[Card] = stack

  def getTrump: Option[Card] = trump

  def getAmount: Int = amount

  def getTurn: Turn = turn

  def getDefended: List[Card] = defended

  def getUndefended: List[Card] = undefended

  def getUsed: List[Card] = used

  def isDenied: Boolean = denied

  def getPassed: Option[Player] = passed

  def status: Status = Status(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)
}
