package model

import play.api.libs.json.*

enum Suit(val display: String, val char: Char):
  case Spades extends Suit("♠", 'S')
  case Hearts extends Suit("♥", 'H')
  case Diamonds extends Suit("♦", 'D')
  case Clubs extends Suit("♣", 'C')

object Suit:
  given Format[Suit] = new Format[Suit] {
    override def writes(suit: Suit): JsValue = JsString(suit.toString)

    override def reads(json: JsValue): JsResult[Suit] = json match {
      case JsString(value) =>
        try {
          JsSuccess(Suit.valueOf(value))
        } catch {
          case _: IllegalArgumentException =>
            JsError(s"Unknown suit: $value")
        }
      case _ => JsError("Expected a JSON string for Suit")
    }
  }