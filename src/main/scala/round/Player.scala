package round

import card.Card

case class Player(name: String, cards: List[Card], turn: Turn)

def getNewPlayer(name: String, cardAmount: Int, givenCards: List[Card], cardGenerator: (Int, List[Card]) => List[Card]): Player = {
  Player(name, cardGenerator.apply(cardAmount, givenCards), Turn.Watching)
}
