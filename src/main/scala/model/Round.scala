package model

case class Round(turn: Turn, defended: List[Card], undefended: List[Card], used: List[Card], passed: Option[Player], firstlyAttacked: Boolean, secondlyAttacked: Boolean)
