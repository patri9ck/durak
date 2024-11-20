package model

import scala.util.Random

case class Group(players: List[Player], stack: List[Card], trump: Card, amount: Int)

object Group {
  def createGroup(amount: Int, names: List[String]): Group = {
    val deck = Card.getDeck

    val index = Random.nextInt(deck.size)
    val trump = deck(index)

    var remaining = deck.patch(index, Nil, 1)

    val players = names.map { name =>
      val playerCards = remaining.take(amount)
      remaining = remaining.drop(amount)
      Player(name, playerCards, Turn.Watching)
    }

    Group(players, remaining :+ trump, trump, amount)
  }
}
