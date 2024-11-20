package model

class StatusBuilder(var players: List[Player],
                    var stack: List[Card],
                    var trump: Card,
                    var amount: Int,
                    var turn: Turn,
                    var defended: List[Card],
                    var undefended: List[Card],
                    var used: List[Card],
                    var denied: Boolean,
                    var passed: Option[Player]) {

  def setPlayers(players: List[Player]): StatusBuilder = {
    this.players = players

    this
  }

  def setStack(stack: List[Card]): StatusBuilder = {
    this.stack = stack

    this
  }

  def setTrump(trump: Card): StatusBuilder = {
    this.trump = trump

    this
  }

  def setAmount(amount: Int): StatusBuilder = {
    this.amount = amount

    this
  }

  def setTurn(turn: Turn): StatusBuilder = {
    this.turn = turn

    this
  }

  def setDefended(defended: List[Card]): StatusBuilder = {
    this.defended = defended

    this
  }

  def setUndefended(undefended: List[Card]): StatusBuilder = {
    this.undefended = undefended

    this
  }

  def setUsed(used: List[Card]): StatusBuilder = {
    this.used = used

    this
  }

  def resetRound: StatusBuilder = {
    turn = Turn.FirstlyAttacking
    defended = List()
    undefended = List()
    used = List()
    denied = false

    this
  }

  def setDenied(denied: Boolean): StatusBuilder = {
    this.denied = denied

    this
  }

  def setPassed(passed: Player): StatusBuilder = {
    this.passed = Some(passed)

    this
  }

  def removePassed(): StatusBuilder = {
    this.passed = None

    this
  }

  def byTurn(turn: Turn): Option[Player] = {
    players.find(_.turn == turn)
  }

  def status: Status = Status(Group(players, stack, trump, amount), Round(turn, defended, undefended, used, denied, passed))
}

object StatusBuilder {

  def create(status: Status): StatusBuilder = StatusBuilder(status.group.players,
    status.group.stack,
    status.group.trump,
    status.group.amount,
    status.round.turn,
    status.round.defended,
    status.round.undefended,
    status.round.used,
    status.round.denied,
    status.round.passed)
}
