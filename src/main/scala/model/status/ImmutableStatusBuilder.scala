package model.status

import com.google.inject.Inject
import com.google.inject.name.Named
import model.{Card, Player, Turn}

class ImmutableStatusBuilder @Inject()(@Named("players") private val players: List[Player],
                                       @Named("stack") private val stack: List[Card],
                                       @Named("trump") private val trump: Option[Card],
                                       @Named("amount") private val amount: Int,
                                       @Named("turn") private val turn: Turn,
                                       @Named("defended") private val defended: List[Card],
                                       @Named("undefended") private val undefended: List[Card],
                                       @Named("used") private val used: List[Card],
                                       @Named("denied") private val denied: Boolean,
                                       @Named("passed") private val passed: Option[Player]
                            ) extends StatusBuilder {

  def this() = this(Nil, Nil, None, 0, Turn.Watching, Nil, Nil, Nil, false, None)

  override def setStatus(status: Status): StatusBuilder =
    ImmutableStatusBuilder(status.players, status.stack, status.trump, status.amount, status.turn, status.defended, status.undefended, status.used, status.denied, status.passed)

  override def setPlayers(players: List[Player]): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setStack(stack: List[Card]): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setTrump(trump: Card): StatusBuilder =
    ImmutableStatusBuilder(players, stack, Some(trump), amount, turn, defended, undefended, used, denied, passed)

  override def removeTrump(): StatusBuilder =
    ImmutableStatusBuilder(players, stack, None, amount, turn, defended, undefended, used, denied, passed)

  override def setAmount(amount: Int): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setTurn(turn: Turn): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setDefended(defended: List[Card]): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setUndefended(undefended: List[Card]): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setUsed(used: List[Card]): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def resetRound: StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)

  override def setDenied(denied: Boolean): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, passed)

  override def setPassed(passed: Player): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, Some(passed))

  override def removePassed(): StatusBuilder =
    ImmutableStatusBuilder(players, stack, trump, amount, turn, defended, undefended, used, denied, None)

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
