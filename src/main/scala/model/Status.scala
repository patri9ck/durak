package model

import scala.util.Random

case class Status(players: List[Player], stack: List[Card], trump: Card, amount: Int, turn: Turn, defended: List[Card], undefended: List[Card], used: List[Card], denied: Boolean, passed: Option[Player])

object Status {
  def createStatus(amount: Int, names: List[String]): Status = {
    val deck = Card.getDeck

    val index = Random.nextInt(deck.size)
    val trump = deck(index)

    var remaining = deck.patch(index, Nil, 1)

    val players = names.map { name =>
      val playerCards = remaining.take(amount)
      remaining = remaining.drop(amount)
      Player(name, playerCards, Turn.Watching)
    }

    Status(players, remaining :+ trump, trump, amount, Turn.Watching, Nil, Nil, Nil, false, None)
  }
}
