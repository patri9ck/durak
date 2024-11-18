package model

case class Card(rank: Rank, suit: Suit) {
  def beats(card: Card): Boolean = {
    if (suit != card.suit) {
      return false
    }
    
    rank.order > card.rank.order
  }
}




