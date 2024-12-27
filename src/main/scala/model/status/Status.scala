package model.status

import model.{Card, Player, Turn}
import play.api.libs.json.{Json, OFormat}

import scala.xml.Elem

case class Status(players: List[Player] = Nil,
                  stack: List[Card] = Nil,
                  trump: Option[Card] = None,
                  amount: Int = 0,
                  turn: Turn = Turn.Uninitialized,
                  defended: List[Card] = Nil,
                  undefended: List[Card] = Nil,
                  used: List[Card] = Nil,
                  denied: Boolean = false,
                  passed: Option[Player] = None) {

  def toXml: Elem = {
    <status>
      <players>
        {players.map(_.toXml)}
      </players>
      <stack>
        {stack.map(_.toXml)}
      </stack>
      <trump>
        {trump.map(_.toXml).getOrElse(Nil)}
      </trump>
      <amount>
        {amount}
      </amount>
      <turn>
        {turn}
      </turn>
      <defended>
        {defended.map(_.toXml)}
      </defended>
      <undefended>
        {undefended.map(_.toXml)}
      </undefended>
      <used>
        {used.map(_.toXml)}
      </used>
      <denied>
        {denied}
      </denied>
      <passed>
        {passed.map(_.toXml).getOrElse(Nil)}
      </passed>
    </status>
  }
}

object Status {
  implicit val statusFormat: OFormat[Status] = Json.format[Status]

  def fromXml(xml: Elem): Status = {
    Status(
      (xml \ "players" \ "player").map(Player.fromXml).toList,
      (xml \ "stack" \ "card").map(Card.fromXml).toList,
      (xml \ "trump" \ "card").headOption.map(Card.fromXml),
      (xml \ "amount").text.toInt,
      Turn.valueOf((xml \ "turn").text),
      (xml \ "defended" \ "card").map(Card.fromXml).toList,
      (xml \ "undefended" \ "card").map(Card.fromXml).toList,
      (xml \ "used" \ "card").map(Card.fromXml).toList,
      (xml \ "denied").text.toBoolean,
      (xml \ "passed" \ "player").headOption.map(Player.fromXml)
    )
  }
}