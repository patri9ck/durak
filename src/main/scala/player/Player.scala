package player

import card.Card

case class Player(name: String, cards: List[Card], state: PlayerState)