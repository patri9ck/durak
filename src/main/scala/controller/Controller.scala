package controller

import model.{Card, Group, Player, Rank, Status, Suit, Turn}
import observer.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait Controller extends Observable {
  
  def status: Status

  def chooseAttacking(attacking: Player): Unit

  def chooseAttacking(): Unit

  def canAttack(card: Card): Boolean

  def denied(): Unit

  def pickUp(): Unit 

  def attack(card: Card): Unit 
  
  def canDefend(used: Card, undefended: Card): Boolean
  
  def defend(used: Card, undefended: Card): Unit

  def byTurn(turn: Turn): Option[Player]
  
  def getPlayer: Option[Player] = status.group.players.find(_.turn == status.round.turn)
}
