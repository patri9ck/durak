package model

import play.api.libs.json.*

enum Rank(val order: Int, val display: String, val char: Char):
  case Two extends Rank(2, "2", '2')
  case Three extends Rank(3, "3", '3')
  case Four extends Rank(4, "4", '4')
  case Five extends Rank(5, "5", '5')
  case Six extends Rank(6, "6", '6')
  case Seven extends Rank(7, "7", '7')
  case Eight extends Rank(8, "8", '8')
  case Nine extends Rank(9, "9", '9')
  case Ten extends Rank(10, "10", 'T')
  case Jack extends Rank(11, "J", 'J')
  case Queen extends Rank(12, "Q", 'Q')
  case King extends Rank(13, "K", 'K')
  case Ace extends Rank(14, "A", 'A')

object Rank {
  def getBiggestRankLength: Int = Rank.values.map(_.display.length).max

  given Format[Rank] = new Format[Rank] {
    override def writes(rank: Rank): JsValue = JsString(rank.toString)

    override def reads(json: JsValue): JsResult[Rank] = json match {
      case JsString(value) =>
        try {
          JsSuccess(Rank.valueOf(value))
        } catch {
          case _: IllegalArgumentException =>
            JsError(s"Unknown rank: $value")
        }
      case _ => JsError("Expected a JSON string for Rank")
    }
  }
}