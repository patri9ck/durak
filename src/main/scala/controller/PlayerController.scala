package controller

import model.{Card, Player, Turn}
import observer.Observable

class PlayerController extends Observable {
  
  
}

object PlayerController {
  def getNewPlayer(name: String, cardAmount: Int, givenCards: List[Card], cardGenerator: (Int, List[Card]) => List[Card]): Player = {
    Player(name, cardGenerator.apply(cardAmount, givenCards), Turn.Watching)
  }
}
