package model.status

import model.{Card, Player, Turn}

class ImmutableStatusBuilder(val players: List[Player] = Nil,
                              val stack: List[Card] = Nil,
                              val trump: Option[Card] = None,
                              val amount: Int = 0,
                              val turn: Turn = Turn.Watching,
                              val defended: List[Card] = Nil,
                              val undefended: List[Card] = Nil,
                              val used: List[Card] = Nil,
                              val denied: Boolean = false,
                              val passed: Option[Player] = None
                            ) extends StatusBuilder {

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

  override def setPlayers(players: List[Player]): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setStack(stack: List[Card]): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setTrump(trump: Card): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, Some(trump), amount, turn, defended, undefended, used, denied, passed)

  override def removeTrump(): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, None, amount, turn, defended, undefended, used, denied, passed)

  override def setAmount(amount: Int): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setTurn(turn: Turn): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setDefended(defended: List[Card]): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setUndefended(undefended: List[Card]): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setUsed(used: List[Card]): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def resetRound: ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)

  override def setDenied(denied: Boolean): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setPassed(passed: Player): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, Some(passed))

  override def removePassed(): ImmutableStatusBuilder =
    new ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, None)

  override def byTurn(turn: Turn): Option[Player] = players.find(_.turn == turn)

  override def getPlayers: List[Player] = players

  override def getStack: List[Card] = stack

  override def getTrump: Option[Card] = trump

  override def getAmount: Int = amount

  override def getTurn: Turn = turn

  override def getDefended: List[Card] = defended

  override def getUndefended: List[Card] = undefended

  override def getUsed: List[Card] = used

  override def isDenied: Boolean = denied

  override def getPassed: Option[Player] = passed

  override def status: Status = Status(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)
}
