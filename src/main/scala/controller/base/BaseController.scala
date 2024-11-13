package controller.base

import model.*
import observer.Observable

import scala.collection.mutable.ListBuffer
import scala.util.Random

// To-do: Methoden als abstrakte Methoden in Controller definieren und hier implementieren
// To-do: Weitere Methoden für Spiellogik hinzufügen
case class BaseController() extends Observable {

  def chooseDefending(group: Group, defendingPlayer: Player): Group = {
    if (group.players.isEmpty) {
      return group
    }

    val defendingIndex = group.players.indexWhere(_ == defendingPlayer)

    if (defendingIndex == -1) {
      return group
    }

    val updatedPlayers = group.players.zipWithIndex.map { case (player, idx) =>
      if (idx == defendingIndex) {
        player.copy(turn = Turn.Defending)
      } else if (group.players.length == 2) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else if (idx == (defendingIndex - 1 + group.players.length) % group.players.length) {
        player.copy(turn = Turn.SecondlyAttacking)
      } else if (idx == (defendingIndex + 1) % group.players.length) {
        player.copy(turn = Turn.FirstlyAttacking)
      } else {
        player.copy(turn = Turn.Watching)
      }
    }
    
    Group(updatedPlayers, group.stack)
  }

  def chooseDefendingRandomly(group: Group): Group = {
    chooseDefending(group, Random.shuffle(group.players).head)
  }
  
  // To-do:
  // Falls Spieler weniger als 6 Karten haben, vom Stack welche entfernen und in richtiger Reihenfolge verteilen
  // Immer mit dem Spieler anfangen, der vor/links von secondlyAttacking ist
  def drawFromStack(group: Group): Group = {
    
  }

  // To-do: 
  // Überprüfen ob ein Spieler keine Karten mehr hart und anschließend auf WATCHING setzen
  // Aktualisierte Gruppe und gewonnener Spieler zurückgeben
  // Falls kein neuer Spieler gewonnen hat, Player auf null setzen über Option
  def removeIfWon(group: Group): (Group, Option[Player]) = {

  }
}





