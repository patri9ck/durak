package model.status

import com.google.inject.Inject
import com.google.inject.name.Named
import model.{Card, Player, Turn}

class MutableStatusBuilder @Inject()(@Named("players") private var players: List[Player],
                                     @Named("stack") private var stack: List[Card],
                                     @Named("trump") private var trump: Option[Card],
                                     @Named("amount") private var amount: Int,
                                     @Named("turn") private var turn: Turn,
                                     @Named("defended") private var defended: List[Card],
                                     @Named("undefended") private var undefended: List[Card],
                                     @Named("used") private var used: List[Card],
                                     @Named("denied") private var denied: Boolean,
                                     @Named("passed") private var passed: Option[Player]) extends StatusBuilder {

  override def setStatus(status: Status): StatusBuilder = {
    players = status.players
    stack = status.stack
    trump = status.trump
    amount = status.amount
    turn = status.turn
    defended = status.defended
    undefended = status.undefended
    used = status.used
    denied = status.denied
    passed = status.passed

    this
  }

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
