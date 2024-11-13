package controller

import model.{Card, Group, Player, Rank, Status, Suit}
import observer.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait Controller extends Observable {
  
  def status: Status

  def createStatus(amount: Int, names: List[String]): Unit

  def chooseDefending(defending: Player): Unit

  def chooseDefendingRandomly(): Unit
  
  def drawFromStack(): Unit

  def removeIfWon(): Option[Player]
  
  def defending(): Option[Player]
}
