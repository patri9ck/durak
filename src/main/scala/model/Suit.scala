package model

enum Suit(val display: String):
  case Spades extends Suit("♠")
  case Hearts extends Suit("♥")
  case Diamonds extends Suit("♦")
  case Clubs extends Suit("♣")

  override def toString: String = display

