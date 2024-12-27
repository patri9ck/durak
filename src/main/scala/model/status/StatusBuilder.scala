package model.status

import model.{Card, Player, Turn}

trait StatusBuilder {

  def setStatus(status: Status): StatusBuilder

  def setPlayers(players: List[Player]): StatusBuilder

  def setStack(stack: List[Card]): StatusBuilder

  def setTrump(trump: Card): StatusBuilder

  def removeTrump(): StatusBuilder

  def setAmount(amount: Int): StatusBuilder

  def setTurn(turn: Turn): StatusBuilder

  def setDefended(defended: List[Card]): StatusBuilder

  def setUndefended(undefended: List[Card]): StatusBuilder

  def setUsed(used: List[Card]): StatusBuilder

  def resetRound: StatusBuilder

  def setDenied(denied: Boolean): StatusBuilder

  def setPassed(passed: Player): StatusBuilder

  def removePassed(): StatusBuilder

  def byTurn(turn: Turn): Option[Player]

  def getPlayers: List[Player]

  def getStack: List[Card]

  def getTrump: Option[Card]

  def getAmount: Int

  def getTurn: Turn

  def getDefended: List[Card]

  def getUndefended: List[Card]

  def getUsed: List[Card]

  def isDenied: Boolean

  def getPassed: Option[Player]

  def status: Status
}
