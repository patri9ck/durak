package controller.base

import controller.Controller
import controller.base.command.{AttackCommand, ChooseAttackingCommand, DefendCommand, DenyCommand, PickUpCommand, StartCommand}
import model.*
import util.{Observable, UndoManager}

import scala.annotation.tailrec
import scala.util.Random

class BaseController(var status: Status = new Status) extends Controller {

  private val undoManager = UndoManager()

  def chooseAttacking(players: List[Player], index: Int): List[Player] = {
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

  override def chooseAttacking(): Unit = {
    chooseAttacking(Random.shuffle(status.players).head)
  }

  override def chooseAttacking(attacking: Player): Unit = {
    undoManager.doStep(ChooseAttackingCommand(this, attacking))

    notifySubscribers()
  }
  
  override def start(amount: Int, names: List[String]): Unit = {
    undoManager.doStep(StartCommand(this, amount, names))

    notifySubscribers()
  }

  override def deny(): Unit = {
    undoManager.doStep(DenyCommand(this))
    
    notifySubscribers()
  }

  
  override def getPlayer: Option[Player] = byTurn(status.turn)
  
  override def byTurn(turn: Turn): Option[Player] = {
    status.players.find(_.turn == turn)
  }

  override def pickUp(): Unit = {
    undoManager.doStep(PickUpCommand(this))

    notifySubscribers()
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

  override def attack(card: Card): Unit = {
    undoManager.doStep(AttackCommand(this, card))

    notifySubscribers()
  }

  override def canAttack(card: Card): Boolean = {
    status.defended.isEmpty && status.undefended.isEmpty
      || status.used.exists(_.rank == card.rank)
      || status.defended.exists(_.rank == card.rank)
      || status.undefended.exists(_.rank == card.rank)
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
  
  override def defend(used: Card, undefended: Card): Unit = {
    undoManager.doStep(DefendCommand(this, used, undefended))

    notifySubscribers()
  }
  
  override def canDefend(used: Card, undefended: Card): Boolean = {
    if (used.beats(undefended)) {
      return true
    }

    if (used.suit == status.trump.suit) {
      return undefended.suit != status.trump.suit
    }

    false
  }

  override def undo(): Unit = {
    undoManager.undoStep()

    notifySubscribers()
  }

  override def redo(): Unit = {
    undoManager.redoStep()

    notifySubscribers()
  }
}
