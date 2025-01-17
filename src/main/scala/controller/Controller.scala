package controller

import model.*
import model.status.Status
import util.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * The controller contains business logic. It holds a status which is updated through its methods executed by the observers.
 * Upon a state update, all observers are notified. Observers can then read the status and reflect the changes.
 * Methods which change
 */
trait Controller extends Observable {

  /**
   * The status contains the current state of the game. It is updated by the controller's methods.
   */
  var status: Status

  /**
   * Initializes the status with the specified card amount and player names. The first attacking player is chosen randomly.
   * The exact update depends on the implementation, normally [[base.command.InitializeCommand]].
   * The method assumes to be called in a valid context and will update all observers
   * @param amount the card amount
   * @param names the player names
   */
  def initialize(amount: Int, names: List[String]): Unit

  /**
   * Initializes the status with the specified card amount, player names and first attacking player.
   * The exact update depends on the implementation, normally [[base.command.InitializeCommand]].
   * The method assumes to be called in a valid context and will update all observers
   *
   * @param amount the card amount
   * @param names  the player names
   * @param attacking the first attacking player
   */
  def initialize(amount: Int, names: List[String], attacking: String): Unit

  /**
   * Should be called when an attacking player denies their attack.
   * The exact update depends on the implementation, normally [[base.command.DenyCommand]].
   * The method assumes to be called in a valid context and will update all observers
   */
  def deny(): Unit

  /**
   * Should be called when a defending player picks cards up, thus failing to defend.
   * The exact update depends on the implementation, normally [[base.command.DefendCommand]].
   * The method assumes to be called in a valid context and will update all observers
   */
  def pickUp(): Unit

  /**
   * Should be called when an attacking player attacks with the specified card. It is not checked whether [[Controller.canAttack]] returns true.
   * The exact update depends on the implementation, normally [[base.command.AttackCommand]].
   * The method assumes to be called in a valid context and will update all observers
   *
   * @param card The card to attack with.
   */
  def attack(card: Card): Unit

  /**
   * Checks whether a card can be used to attack with.
   * @param card the card to attack with
   * @return true when either [[Status.used]], [[Status.undefended]] or [[Status.defended]] contain the specified card. Otherwise, false.
   */
  def canAttack(card: Card): Boolean

  /**
   * Should be called when a defending player attacks an undefended card with one of their own. It is not checked whether [[controller.Controller.canDefend]] returns true.
   * The exact update depends on the implementation, normally [[controller.base.command.DefendCommand]].
   * The method assumes to be called in a valid context and will update all observers
   *
   * @param used The card of the defending player
   * @param undefended The undefended card to defend
   */
  def defend(used: Card, undefended: Card): Unit

  /**
   * Checks whether a card can be used to defend another.
   *
   * @param used the card of the defending player
   * @param undefended the undefended card to defend
   * @return true when [[Card.beats]], called on [[used]] with [[undefended]] as the argument, returns true or when [[used]] is a trump card and [[undefended]] is not. Otherwise, false.
   */
  def canDefend(used: Card, undefended: Card): Boolean

  /**
   * Returns the player who has the specified turn.
   * @return the player who has the specified turn. If multiple players have the same turn, the first one is returned.
   */
  def byTurn(turn: Turn): Option[Player]

  /**
   * Returns the player who has the current turn.
   * @return the player who has the current turn. The current turn is determined by [[model.status.Status.turn]].
   */
  def current: Option[Player]

  /**
   * Undoes the last command.
   * The method assumes to be called in a valid context and will update all observers
   */
  def undo(): Unit

  /**
   * Redoes the last undone command.
   * The method assumes to be called in a valid context and will update all observers
   */
  def redo(): Unit

  /**
   * Loads the status from I/O, normally calling [[model.io.FileIo.load]] and thus might block.
   * All exceptions will be caught.
   * The method assumes to be called in a valid context and will update all observers
   */
  def load(): Unit

  /**
   * Saves the status to I/O, normally calling [[model.io.FileIo.save(Status)]] and thus might block.
   * All exceptions will be caught.
   * The method assumes to be called in a valid context and will update all observers
   */
  def save(): Unit

  /**
   * Returns whether the game is over.
   * @return true when there is only one player whose turn is not [[model.Turn.Finished]]. Otherwise, false.
   */
  def isOver: Boolean

  /**
   * Unbinds any I/O, normally calling [[model.io.FileIo.unbind]].
   */
  def unbind(): Unit
}
