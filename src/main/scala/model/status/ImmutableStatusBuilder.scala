package model.status

import com.google.inject.Inject
import model.{Card, Player, Turn}

/**
 * An immutable implementation of [[model.status.StatusBuilder]]
 *
 * @param players    a list of all players, initially set to [[Nil]]
 * @param stack      a list of all cards, representing the stack, initially set to [[Nil]]
 * @param trump      an [[Option]] containing the trump. The trump is the last element on the [[stack]], initially set to [[None]].
 * @param amount     the amount of cards each player should have while the [[stack]] is non-empty, initially set to 0.
 * @param turn       the current turn, initially set [[model.Turn.Uninitialized]]
 * @param defended   a list of all currently defended cards, initially set to [[Nil]]
 * @param undefended a list of all currently undefended cards, initially set to [[Nil]]
 * @param used       a list of all currently cards used to defend, initially set to [[Nil]]
 * @param denied     whether the player with the turn [[model.turn.FirstlyAttacking]] denied his attack, initially set to false
 * @param passed     the player who initially passed cards of the same rank to the next player, initially set to [[None]]
 */
class ImmutableStatusBuilder(private val players: List[Player] = Nil,
                             private val stack: List[Card] = Nil,
                             private val trump: Option[Card] = None,
                             private val amount: Int = 0,
                             private val turn: Turn = Turn.Uninitialized,
                             private val defended: List[Card] = Nil,
                             private val undefended: List[Card] = Nil,
                             private val used: List[Card] = Nil,
                             private val denied: Boolean = false,
                             private val passed: Option[Player] = None
                            ) extends StatusBuilder {

  /**
   * Sets all values to their default.
   */
  def this() = this(Nil, Nil, None, 0, Turn.Uninitialized, Nil, Nil, Nil, false, None)

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
