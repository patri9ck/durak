package model.status

import model.{Card, Player, Turn}

/**
 * A builder for [[model.status.Status]]
 */
trait StatusBuilder {

  /**
   * Sets all values to the values provided by the specified status.
   * @param status the status to read from
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setStatus(status: Status): StatusBuilder

  /**
   * Sets the players to the specified player list.
   * @param players the players to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setPlayers(players: List[Player]): StatusBuilder

  /**
   * Sets the stack to the specified card list.
   * @param stack the stack to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setStack(stack: List[Card]): StatusBuilder

  /**
   * Sets the trump to the specified card.
   * @param trump the trump to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setTrump(trump: Card): StatusBuilder

  /**
   * Sets the trump to [[None]]
   * @return an updated [[model.status.StatusBuilder]]
   */
  def removeTrump(): StatusBuilder

  /**
   * Sets the amount to the specified amount.
   * @param amount the amount to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setAmount(amount: Int): StatusBuilder

  /**
   * Sets the turn to the specified turn.
   * @param turn the turn to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setTurn(turn: Turn): StatusBuilder

  /**
   * Sets the defended cards to the specified card list.
   * @param defended the defended cards to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setDefended(defended: List[Card]): StatusBuilder

  /**
   * Sets the undefended cards to the specified card list.
   * @param undefended the undefended cards to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setUndefended(undefended: List[Card]): StatusBuilder

  /**
   * Sets the used cards to the specified card list.
   * @param used the used cards to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setUsed(used: List[Card]): StatusBuilder

  /**
   * Resets the rounds by setting the turn to [[model.Turn.FirstlyAttacking]] and the defended, undefended and used cards to empty lists. The denied flag is set to false.
   * @return an updated [[model.status.StatusBuilder]]
   */
  def resetRound: StatusBuilder

  /**
   * Sets the denied flag to the specified value.
   * @param denied the denied flag to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setDenied(denied: Boolean): StatusBuilder

  /**
   * Sets the passed player to the specified player.
   * @param passed the passed player to set
   * @return an updated [[model.status.StatusBuilder]]
   */
  def setPassed(passed: Player): StatusBuilder

  /**
   * Sets the passed player to [[None]].
   * @return an updated [[model.status.StatusBuilder]]
   */
  def removePassed(): StatusBuilder

  /**
   * Retrieves a player by its turn.
   * @param turn the turn to search for
   * @return the player with the specified turn. If no player has the specified turn, [[None]] is returned.
   */
  def byTurn(turn: Turn): Option[Player]

  /**
   * Retrieves the players.
   * @return the players
   */
  def getPlayers: List[Player]

  /**
   * Retrieves the stack.
   * @return the stack
   */
  def getStack: List[Card]

  /**
   * Retrieves the trump.
   * @return the trump. If no trump is set, [[None]] is returned.
   */
  def getTrump: Option[Card]

  /**
   * Retrieves the card amount.
   * @return the card amount
   */
  def getAmount: Int

  /**
   * Retrieves the current turn.
   * @return the current turn
   */
  def getTurn: Turn

  /**
   * Retrieves the defended cards.
   * @return the defended cards
   */
  def getDefended: List[Card]

  /**
   * Retrieves the undefended cards.
   * @return the undefended cards
   */
  def getUndefended: List[Card]

  /**
   * Retrieves the used cards.
   * @return the used cards
   */
  def getUsed: List[Card]

  /**
   * Retrieves the denied flag.
   * @return the denied flag
   */
  def isDenied: Boolean

  /**
   * Retrieves the passed player.
   * @return the passed player. If no player passed, [[None]] is returned.
   */
  def getPassed: Option[Player]

  /**
   * Builds the status.
   * @return the status
   */
  def status: Status
}
