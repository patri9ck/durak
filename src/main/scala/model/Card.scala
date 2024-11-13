package model

case class Card(rank: Rank, suit: Suit) {
  def beats(card: Card): Boolean = {
    if (suit != card.suit) {
      return false
    }
    
    rank.order > card.rank.order
  }
}

object Card {
  def getRandomCard: Card = Card(Rank.getRandomRank, Suit.getRandomSuit)

  def getRandomCards(amount: Int): List[Card] = List.fill(amount)(getRandomCard)

  def getRandomCards(amount: Int, givenCards: List[Card]): List[Card] = {
    Iterator.continually(getRandomCard)
      .filterNot(givenCards.contains)
      .take(amount)
      .toList
  }
}




