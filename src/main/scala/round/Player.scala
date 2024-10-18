package round

import card.Card

case class Player(name: String, cards: List[Card])

def getNewPlayer(name: String, cardAmount: Int, cardGenerator: Int => List[Card]): Player = {
  Player(name, cardGenerator.apply(cardAmount))
}
