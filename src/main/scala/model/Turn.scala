package model

import play.api.libs.json.*

enum Turn(val name: String) {
  case Defending extends Turn("Verteidigen")
  case FirstlyAttacking extends Turn("Primär Angreifen")
  case SecondlyAttacking extends Turn("Sekundär Angreifen")
  case Watching extends Turn("Zuschauen")
  case Finished extends Turn("Fertig")
  case Uninitialized extends Turn("Uninitialisiert")
}

object Turn {
  given Format[Turn] = new Format[Turn] {
    override def writes(turn: Turn): JsValue = JsString(turn.toString)

    override def reads(json: JsValue): JsResult[Turn] = json match {
      case JsString(value) =>
        try {
          JsSuccess(Turn.valueOf(value))
        } catch {
          case _: IllegalArgumentException =>
            JsError(s"Unknown Turn: $value")
        }
      case _ => JsError("Expected a JSON string for Turn")
    }
  }
}