package model.status

import model.{Card, Player, Turn}

/**
 * A mutable implementation of [[model.status.StatusBuilder]]
 * @param players a list of all players, initially set to [[Nil]]
 * @param stack a list of all cards, representing the stack, initially set to [[Nil]]
 * @param trump an [[Option]] containing the trump. The trump is the last element on the [[stack]], initially set to [[None]].
 * @param amount the amount of cards each player should have while the [[stack]] is non-empty, initially set to 0.
 * @param turn the current turn, initially set [[model.Turn.Uninitialized]]
 * @param defended a list of all currently defended cards, initially set to [[Nil]]
 * @param undefended a list of all currently undefended cards, initially set to [[Nil]]
 * @param used a list of all currently cards used to defend, initially set to [[Nil]]
 * @param denied whether the player with the turn [[model.turn.FirstlyAttacking]] denied his attack, initially set to false
 * @param passed the player who initially passed cards of the same rank to the next player, initially set to [[None]]
 */
class MutableStatusBuilder(private var players: List[Player] = Nil,
                           private var stack: List[Card] = Nil,
                           private var trump: Option[Card] = None,
                           private var amount: Int = 0,
                           private var turn: Turn = Turn.Uninitialized,
                           private var defended: List[Card] = Nil,
                           private var undefended: List[Card] = Nil,
                           private var used: List[Card] = Nil,
                           private var denied: Boolean = false,
                           private var passed: Option[Player] = None) extends StatusBuilder {

  /**
   * Sets all values to their default.
   */
  def this() = this(Nil, Nil, None, 0, Turn.Uninitialized, Nil, Nil, Nil, false, None)

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
    defended = Nil
    undefended = Nil
    used = Nil
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
