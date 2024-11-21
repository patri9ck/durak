package model

case class Player(name: String, cards: List[Card], turn: Turn) {
  override def toString: String = name
}

