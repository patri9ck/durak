package model.status

import model.{Card, Player, Turn}

import scala.util.Random

case class Status(players: List[Player] = Nil,
                  stack: List[Card] = Nil,
                  trump: Option[Card] = None,
                  amount: Int = 0,
                  turn: Turn = Turn.Uninitialized,
                  defended: List[Card] = Nil,
                  undefended: List[Card] = Nil,
                  used: List[Card] = Nil,
                  denied: Boolean = false,
                  passed: Option[Player] = None) {

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