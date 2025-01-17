package model.status

import model.{Card, Player, Turn}
import play.api.libs.json.{Json, OFormat}

import scala.xml.Elem

/**
 * Contains the current status of the game.
 * @param players a list of all players, initially set to [[Nil]]
 * @param stack a list of all cards, representing the stack, initially set to [[Nil]]
 * @param trump an [[Option]] containing the trump. The trump is the last element on the [[stack]], initially set to [[None]].
 * @param amount the amount of cards each player should have while the [[stack]] is non-empty, initially set to 0.
 * @param turn the current turn, initially set [[model.Turn.Uninitialized]]
 * @param defended a list of all currently defended cards, initially set to [[Nil]]
 * @param undefended a list of all currently undefended cards, initially set to [[Nil]]
 * @param used a list of all currently cards used to defend, initially set to [[Nil]]
 * @param denied whether the player with the turn [[model.turn.FirstlyAttacking]] denied his attack, initially set to false
 * @param passed the player who initially passed cards of the same rank to the next player, initially set to [[None]]
 */
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

  def fromXml(elem: Elem): Status = {
    Status(
      (elem \ "players" \ "player").map(Player.fromXml).toList,
      (elem \ "stack" \ "card").map(Card.fromXml).toList,
      (elem \ "trump" \ "card").headOption.map(Card.fromXml),
      (elem \ "amount").text.trim.toInt,
      Turn.valueOf((elem \ "turn").text.trim),
      (elem \ "defended" \ "card").map(Card.fromXml).toList,
      (elem \ "undefended" \ "card").map(Card.fromXml).toList,
      (elem \ "used" \ "card").map(Card.fromXml).toList,
      (elem \ "denied").text.trim.toBoolean,
      (elem \ "passed" \ "player").headOption.map(Player.fromXml)
    )
  }
}