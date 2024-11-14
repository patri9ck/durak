package controller

import model.{Card, Group, Player, Rank, Status, Suit, Turn}
import observer.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait Controller extends Observable {
  
  def status: Status

  def chooseDefending(defending: Player): Unit

  def chooseDefending(): Unit

  def canAttack(card: Card): Boolean

  def denied(): Unit

  def pickUp(): Unit 

  def attack(card: Card): Unit 
  
  def canDefend(used: Card, undefended: Card): Boolean
  
  def defend(used: Card, undefended: Card): Unit

  def byTurn(turn: Turn): Option[Player]
}
