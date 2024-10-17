package card

import scala.util.Random

enum Suit(val display: String):
  case Spades extends Suit("♠")
  case Hearts extends Suit("♥")
  case Diamonds extends Suit("♦")
  case Clubs extends Suit("♣")

def getRandomSuit: Suit = Suit.values(Random.nextInt(Suit.values.length))