package card

case class Card(value: Value, suit: Suit)

def getRandomCard: Card = Card(getRandomValue, getRandomSuit)

def getRandomCards(n: Int) : List[Card] = List.fill(n)(getRandomCard)
