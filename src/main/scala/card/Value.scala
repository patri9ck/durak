package card

import scala.util.Random

enum Value(val order: Int, val display: String):
  case Two extends Value(2, "2")
  case Three extends Value(3, "3")
  case Four extends Value(4, "4")
  case Five extends Value(5, "5")
  case Six extends Value(6, "6")
  case Seven extends Value(7, "7")
  case Eight extends Value(8, "8")
  case Nine extends Value(9, "9")
  case Ten extends Value(10, "10")
  case Jack extends Value(11, "J")
  case Queen extends Value(12, "Q")
  case King extends Value(13, "K")
  case Ace extends Value(14, "A")

def getBiggestValueLength: Int = Value.values.map(_.display.length).max

def getRandomValue: Value = Value.values(Random.nextInt(Value.values.length))
