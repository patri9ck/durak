package model

case class Player(name: String, cards: List[Card], turn: Turn)

object Player {
  def getNewPlayer(name: String, cardAmount: Int, givenCards: List[Card], cardGenerator: (Int, List[Card]) => List[Card]): Player = {
    Player(name, cardGenerator.apply(cardAmount, givenCards), Turn.Watching)
  }
}

