package model

import play.api.libs.json.{Json, OFormat}

import scala.xml.{Elem, Node}

case class Player(name: String, cards: List[Card], turn: Turn) {
  override def toString: String = name

  def toXml: Elem = {
    <player>
      <name>
        {name}
      </name>
      <cards>
        {cards.map(_.toXml)}
      </cards>
      <turn>
        {turn}
      </turn>
    </player>
  }
}

object Player {
  implicit val playerFormat: OFormat[Player] = Json.format[Player]

  def fromXml(node: Node): Player = {
    Player(
      (node \ "name").text,
      (node \ "cards" \ "card").map(Card.fromXml).toList,
      Turn.valueOf((node \ "turn").text)
    )
  }
}

