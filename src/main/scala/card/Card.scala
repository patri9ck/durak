package card

case class Card(rank: Rank, suit: Suit)

def getRandomCard: Card = Card(getRandomRank, getRandomSuit)

def getRandomCards(n: Int) : List[Card] = List.fill(n)(getRandomCard)
