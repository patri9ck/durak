package model.status

import model.{Card, Player, Turn}

import scala.util.Random

case class Status(players: List[Player], stack: List[Card], trump: Option[Card], amount: Int, turn: Turn, defended: List[Card], undefended: List[Card], used: List[Card], denied: Boolean, passed: Option[Player]) {
  def this() = this(Nil, Nil, None, 0, Turn.Uninitialized, Nil, Nil, Nil, false, None)

  def initialize(amount: Int, names: List[String]): Status = {
    val deck = Card.getDeck

    val index = Random.nextInt(deck.size)
    val trump = deck(index)

    var remaining = deck.patch(index, Nil, 1)

    val players = names.map { name =>
      val playerCards = remaining.take(amount)
      remaining = remaining.drop(amount)
      Player(name, playerCards, Turn.Watching)
    }

    copy(players = players, stack = remaining, trump = Some(trump), amount = amount, turn = Turn.Initialized)
  }
}