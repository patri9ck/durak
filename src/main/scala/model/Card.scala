package model

import scala.util.Random

case class Card(rank: Rank, suit: Suit) {
  def beats(card: Card): Boolean = {
    if (suit != card.suit) {
      return false
    }
    
    rank.order > card.rank.order
  }

  override def toString: String = {
    val biggestLength = Rank.getBiggestRankLength

      "┌" + "─" * (biggestLength * 2 + 1) + "┐\n"
      + "│" + rank + " " * (2 * biggestLength + 1 - rank.display.length) + "│\n"
      + "│" + " " * biggestLength + suit + " " * biggestLength + "│\n"
      + "│" + " " * (2 * biggestLength + 1 - rank.display.length) + rank + "│\n"
      + "└" + "─" * (biggestLength * 2 + 1) + "┘"
  }
}

object Card {
  def getDeck: List[Card] = Random.shuffle(for {
      suit <- Suit.values
      rank <- Rank.values
    } yield Card(rank, suit)).toList
}




