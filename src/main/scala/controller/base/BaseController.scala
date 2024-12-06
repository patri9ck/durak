package controller.base

import controller.{Controller, StatusEvent}
import controller.base.command.{AttackCommand, ChooseAttackingCommand, DefendCommand, DenyCommand, InitializeCommand, PickUpCommand}
import model.*
import util.UndoManager

import scala.annotation.tailrec
import scala.swing.Publisher
import scala.util.Random

class BaseController(@volatile var status: Status = new Status) extends Controller {

  private val undoManager = UndoManager()

  def chooseAttacking(players: List[Player], index: Int): List[Player] = {
    require(index >= 0 && index < players.length)
    
    players.zipWithIndex.map { case (player, idx) =>
      if (player.turn == Turn.Finished) {
        player
      } else {
        @tailrec
        def findPreviousActive(currentIndex: Int, steps: Int): Int = {
          if (steps == 0) currentIndex
          else {
            val prevIndex = (currentIndex - 1 + players.length) % players.length
            if (players(prevIndex).turn == Turn.Finished)
              findPreviousActive(prevIndex, steps)
            else
              findPreviousActive(prevIndex, steps - 1)
          }
        }

        val firstlyAttackingIndex = index
        val defendingIndex = findPreviousActive(firstlyAttackingIndex, 1)
        val secondlyAttackingIndex = findPreviousActive(defendingIndex, 1)

        if (idx == firstlyAttackingIndex) {
          player.copy(turn = Turn.FirstlyAttacking)
        } else if (idx == defendingIndex) {
          player.copy(turn = Turn.Defending)
        } else if (idx == secondlyAttackingIndex) {
          player.copy(turn = Turn.SecondlyAttacking)
        } else {
          player.copy(turn = Turn.Watching)
        }
      }
    }
  }

  def chooseNextAttacking(players: List[Player], previous: Player): List[Player] =
    chooseAttacking(players, (players.indexOf(previous) - 1 + players.size) % players.size)

  def updatePlayers(players: List[Player], old: Player, updated: Player): List[Player] = {
    players.map { player =>
      if (old == player) {
        updated
      } else {
        player
      }
    }
  }

  def drawFromStack(statusBuilder: StatusBuilder): Unit = {
    require(statusBuilder.getPassed.isDefined || statusBuilder.byTurn(Turn.FirstlyAttacking).isDefined)
    
    val start = statusBuilder.getPassed.orElse(statusBuilder.byTurn(Turn.FirstlyAttacking)).map(statusBuilder.getPlayers.indexOf).get

    var updatedStack = statusBuilder.getStack
    var updatedPlayers = statusBuilder.getPlayers

    for (step <- statusBuilder.getPlayers.indices) {
      val index = (start - step + statusBuilder.getPlayers.size) % statusBuilder.getPlayers.size
      val player = statusBuilder.getPlayers(index)
      val amount = statusBuilder.getAmount - player.cards.length

      if (player.turn != Turn.Watching && amount > 0) {
        val (draw, remaining) = updatedStack.splitAt(amount)

        updatedStack = remaining
        updatedPlayers = updatedPlayers.updated(index, player.copy(cards = player.cards ++ draw))
      }
    }

    statusBuilder
      .setStack(updatedStack)
      .setPlayers(updatedPlayers)
      .removePassed()
  }

  def hasFinished(finished: Player, statusBuilder: StatusBuilder): Boolean = {
    if (finished.turn == Turn.FirstlyAttacking || finished.turn == Turn.SecondlyAttacking) {
      return statusBuilder.getStack.isEmpty && finished.cards.isEmpty
    }

    if (finished.turn == Turn.Defending) {
      return statusBuilder.getStack.isEmpty && statusBuilder.getUndefended.isEmpty && finished.cards.isEmpty
    }

    true
  }

  def finish(finished: Player, statusBuilder: StatusBuilder): Unit = {
    val updated = finished.copy(turn = Turn.Finished)

    statusBuilder.setPlayers(updatePlayers(statusBuilder.getPlayers, finished, updated))

    if (finished.turn == Turn.Defending) {
      statusBuilder
        .setPlayers(chooseNextAttacking(statusBuilder.getPlayers, updated))
        .resetRound
    } else if (finished.turn == Turn.FirstlyAttacking && statusBuilder.byTurn(Turn.SecondlyAttacking).isEmpty || finished.turn == Turn.SecondlyAttacking) {
      statusBuilder.setTurn(Turn.Defending)
    } else {
      statusBuilder.setTurn(Turn.SecondlyAttacking)
    }
  }

  override def chooseAttacking(): Unit = synchronized {
    chooseAttacking(Random.shuffle(status.players).head)
  }

  override def chooseAttacking(attacking: Player): Unit = synchronized {
    undoManager.doStep(ChooseAttackingCommand(this, attacking))

    publish(StatusEvent())
  }

  override def initialize(amount: Int, names: List[String]): Unit = synchronized {
    undoManager.doStep(InitializeCommand(this, amount, names))
    
    publish(StatusEvent())
  }

  override def deny(): Unit = synchronized {
    undoManager.doStep(DenyCommand(this))
    
    publish(StatusEvent())
  }

  override def pickUp(): Unit = synchronized {
    undoManager.doStep(PickUpCommand(this))

    publish(StatusEvent())
  }

  override def attack(card: Card): Unit = synchronized {
    undoManager.doStep(AttackCommand(this, card))

    publish(StatusEvent())
  }

  override def canAttack(card: Card): Boolean = {
    status.defended.isEmpty && status.undefended.isEmpty
      || status.used.exists(_.rank == card.rank)
      || status.defended.exists(_.rank == card.rank)
      || status.undefended.exists(_.rank == card.rank)
  }
  
  override def defend(used: Card, undefended: Card): Unit = synchronized {
    undoManager.doStep(DefendCommand(this, used, undefended))

    publish(StatusEvent())
  }
  
  override def canDefend(used: Card, undefended: Card): Boolean = {
   require(status.trump.isDefined)
   
    if (used.beats(undefended)) {
      return true
    }

    if (used.suit == status.trump.get.suit) {
      return undefended.suit != status.trump.get.suit
    }

    false
  }

  override def byTurn(turn: Turn): Option[Player] = {
    status.players.find(_.turn == turn)
  }

  override def current: Option[Player] = byTurn(status.turn)

  override def undo(): Unit = synchronized {
    undoManager.undoStep()

    publish(StatusEvent())
  }

  override def redo(): Unit = synchronized {
    undoManager.redoStep()

    publish(StatusEvent())
  }
}
