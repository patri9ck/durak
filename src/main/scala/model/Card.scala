package model

import play.api.libs.json.{Json, OFormat}

import scala.util.Random
import scala.xml.{Elem, Node}

case class Card(rank: Rank, suit: Suit) {
  def beats(card: Card): Boolean = {
    if (suit != card.suit) {
      return false
    }

    rank.order > card.rank.order
  }

  override def toString: String = {
    val biggestLength = Rank.getBiggestRankLength

    "┌" + "─" * (biggestLength * 2 + 1) + "┐\n"
      + "│" + rank.display + " " * (2 * biggestLength + 1 - rank.display.length) + "│\n"
      + "│" + " " * biggestLength + suit.display + " " * biggestLength + "│\n"
      + "│" + " " * (2 * biggestLength + 1 - rank.display.length) + rank.display + "│\n"
      + "└" + "─" * (biggestLength * 2 + 1) + "┘"
  }

  def getPath: String = getClass.getResource("/cards/" + rank.char + suit.char + ".png").toString

  def toSelectableCard: SelectableCard = SelectableCard(this)

  def toXml: Elem = {
    <card>
      <rank>
        {rank}
      </rank>
      <suit>
        {suit}
      </suit>
    </card>
  }
}

object Card {
  def getDeck: List[Card] = Random.shuffle(for {
    suit <- Suit.values
    rank <- Rank.values
  } yield Card(rank, suit)).toList

  def toSelectableCards(cards: List[Card]): List[SelectableCard] = cards.map(_.toSelectableCard)

  implicit val cardFormat: OFormat[Card] = Json.format[Card]

  def fromXml(node: Node): Card = {
    Card(
      Rank.valueOf((node \ "rank").text.trim),
      Suit.valueOf((node \ "suit").text.trim)
    )
  }
}