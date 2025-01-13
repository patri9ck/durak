package controller

import model.*
import model.status.Status
import util.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait Controller extends Observable {

  var status: Status

  def initialize(amount: Int, names: List[String]): Unit

  def initialize(amount: Int, names: List[String], attacking: String): Unit

  def deny(): Unit

  def pickUp(): Unit 

  def attack(card: Card): Unit

  def canAttack(card: Card): Boolean

  def defend(used: Card, undefended: Card): Unit
  
  def canDefend(used: Card, undefended: Card): Boolean

  def byTurn(turn: Turn): Option[Player]

  def current: Option[Player]

  def undo(): Unit

  def redo(): Unit

  def load(): Unit

  def save(): Unit
  
  def isOver: Boolean

  def unbind(): Unit
}
