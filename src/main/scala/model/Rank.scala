package model

enum Rank(val order: Int, val display: String):
  case Two extends Rank(2, "2")
  case Three extends Rank(3, "3")
  case Four extends Rank(4, "4")
  case Five extends Rank(5, "5")
  case Six extends Rank(6, "6")
  case Seven extends Rank(7, "7")
  case Eight extends Rank(8, "8")
  case Nine extends Rank(9, "9")
  case Ten extends Rank(10, "10")
  case Jack extends Rank(11, "J")
  case Queen extends Rank(12, "Q")
  case King extends Rank(13, "K")
  case Ace extends Rank(14, "A")

object Rank {
  def getBiggestRankLength: Int = Rank.values.map(_.display.length).max
}




