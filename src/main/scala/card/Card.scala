package card

case class Card(rank: Rank, suit: Suit)

def getRandomCard: Card = Card(getRandomRank, getRandomSuit)

def getRandomCards(amount: Int) : List[Card] = List.fill(amount)(getRandomCard)
