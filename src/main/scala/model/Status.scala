package model

import scala.util.Random

case class Status(group: Group, round: Round)

object Status {
  def createStatus(amount: Int, names: List[String]): Status = {
    val fullDeck: Array[Card] = for {
      suit <- Suit.values
      rank <- Rank.values
    } yield Card(rank, suit)

    val shuffledDeck = scala.util.Random.shuffle(fullDeck)

    val index = Random.nextInt(shuffledDeck.size)
    val trump = shuffledDeck(index)

    var remainingDeck = shuffledDeck.patch(index, Nil, 1)

    val players = names.map { name =>
      val playerCards = remainingDeck.take(amount)
      remainingDeck = remainingDeck.drop(amount)
      Player(name, playerCards.toList, Turn.Watching)
    }

    Status(
      Group(players, remainingDeck.toList, trump, amount),
      Round(Turn.Watching, List(), List(), List(), false, None),
    )
  }
}
