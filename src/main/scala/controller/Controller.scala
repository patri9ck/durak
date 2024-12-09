package controller

import model.*
import util.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait Controller extends Observable {

  def status: Status
  
  def initialize(amount: Int, names: List[String]): Unit

  def chooseAttacking(attacking: Player): Unit

  def chooseAttacking(): Unit

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
}
