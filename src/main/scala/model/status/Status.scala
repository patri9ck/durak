package model.status

import model.{Card, Player, Turn}

case class Status(players: List[Player] = Nil,
                  stack: List[Card] = Nil,
                  trump: Option[Card] = None,
                  amount: Int = 0,
                  turn: Turn = Turn.Uninitialized,
                  defended: List[Card] = Nil,
                  undefended: List[Card] = Nil,
                  used: List[Card] = Nil,
                  denied: Boolean = false,
                  passed: Option[Player] = None)