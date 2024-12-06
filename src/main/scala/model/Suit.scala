package model

enum Suit(val display: String, val char: Char):
  case Spades extends Suit("♠", 'S')
  case Hearts extends Suit("♥", 'H')
  case Diamonds extends Suit("♦", 'D')
  case Clubs extends Suit("♣", 'C')

  override def toString: String = display