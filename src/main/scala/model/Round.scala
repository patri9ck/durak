package model

case class Round(turn: Turn, defended: List[Card], undefended: List[Card], used: List[Card], denied: Boolean, passed: Option[Player])
