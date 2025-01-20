package controller.base

import com.google.inject.{Inject, Singleton}
import controller.Controller
import controller.base.command.*
import model.*
import model.io.FileIo
import model.status.{Status, StatusBuilder}
import util.{Observable, UndoManager}

import scala.annotation.tailrec
import scala.util.Random

/**
 * This is the base implementation of [[controller.Controller]]. Every method that does not implement [[controller.Controller]] does not change the status directly, but rather often acts upon a [[model.status.StatusBuilder]] object.
 * @param fileIo the [[model.io.FileIo]] used for saving and loading the status
 */
@Singleton
class BaseController @Inject()(val fileIo: FileIo) extends Controller {

  private val undoManager = UndoManager()
  var status: Status = Status()

  /**
   * Draws cards from the stack and fills every player's cards up to the set card amount. It will either start with [[model.status.StatusBuilder.getPassed]] or with the player who is [[Turn.FirstlyAttacking]].
   * @param statusBuilder a [[model.status.StatusBuilder]] containing the initial status
   * @return a [[model.status.StatusBuilder]] containing a status with cards drawn from the stack
   */
  def drawFromStack(statusBuilder: StatusBuilder): StatusBuilder = {
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

  /**
   * Checks whether the specified player is finished. A player is finished if he is attacking, having no cards left with the stack being empty. A player can also be finished if he is defending, has no cards left without there being undefended cards or cards on the stack.
   * @param finished the player to check for being finished
   * @param statusBuilder a [[model.status.StatusBuilder]] containing the current status
   * @return whether the specified player is finished
   */
  def hasFinished(finished: Player, statusBuilder: StatusBuilder): Boolean = {
    if (finished.turn == Turn.Finished) {
      return true
    }

    if (finished.turn == Turn.FirstlyAttacking || finished.turn == Turn.SecondlyAttacking) {
      return statusBuilder.getStack.isEmpty && finished.cards.isEmpty
    }

    if (finished.turn == Turn.Defending) {
      return statusBuilder.getStack.isEmpty && statusBuilder.getUndefended.isEmpty && finished.cards.isEmpty
    }

    false
  }

  /**
   * Sets a player to finished and updates all other data accordingly. This method does not check whether [[hasFinished]] returns true.
   * @param finished the player to be set to finished
   * @param statusBuilder a [[model.status.StatusBuilder]] containing the current status
   * @return an updated [[model.status.StatusBuilder]] containing the changes made after the specified player was set to finished
   */
  def finish(finished: Player, statusBuilder: StatusBuilder): StatusBuilder = {
    val updated = finished.copy(turn = Turn.Finished)

    var updatedStatusBuilder = statusBuilder.setPlayers(updatePlayers(statusBuilder.getPlayers, finished, updated))

    if (finished.turn == Turn.Defending) {
      updatedStatusBuilder = statusBuilder
        .setPlayers(chooseNextAttacking(updatedStatusBuilder.getPlayers, updated))
        .resetRound
    } else if (finished.turn == Turn.FirstlyAttacking && statusBuilder.byTurn(Turn.SecondlyAttacking).isEmpty || finished.turn == Turn.SecondlyAttacking) {
      updatedStatusBuilder = statusBuilder.setTurn(Turn.Defending)
    } else {
      updatedStatusBuilder = statusBuilder.setTurn(Turn.SecondlyAttacking)
    }

    updatedStatusBuilder
  }

  /**
   * Sets the specified player to [[model.turn.FirstlyAttacking]] and all other players' turn accordingly.
   * @param players all players, must contain [[previous]]
   * @param previous the player to set to [[model.turn.FirstlyAttacking]]
   * @return a list of players with updated turns
   */
  def chooseNextAttacking(players: List[Player], previous: Player): List[Player] =
    chooseAttacking(players, (players.indexOf(previous) - 1 + players.size) % players.size)

  /**
   * Sets the player at [[index]] to [[model.turn.FirstlyAttacking]] and all other players' turn accordingly.
   * @param players  all players, must contain [[previous]]
   * @param index the index of the player to set to [[model.turn.FirstlyAttacking]]
   * @return a list of players with updated turns
   */
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

  /**
   * Replaces [[old]] with [[updated]] in [[players]]
   * @param players list of players to update
   * @param old the player to be replaced with
   * @param updated the player to replace [[old]]
   * @return an updated list
   */
  def updatePlayers(players: List[Player], old: Player, updated: Player): List[Player] = {
    players.map { player =>
      if (old == player) {
        updated
      } else {
        player
      }
    }
  }

  override def initialize(amount: Int, names: List[String]): Unit = {
    initialize(amount, names, Random.shuffle(names).head)
  }

  override def initialize(amount: Int, names: List[String], attacking: String): Unit = {
    undoManager.doStep(InitializeCommand(this, amount, names, attacking))

    notifySubscribers()
  }

  override def deny(): Unit = {
    undoManager.doStep(DenyCommand(this))

    notifySubscribers()
  }

  override def pickUp(): Unit = {
    undoManager.doStep(PickUpCommand(this))

    notifySubscribers()
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

  override def defend(used: Card, undefended: Card): Unit = {
    undoManager.doStep(DefendCommand(this, used, undefended))

    notifySubscribers()
  }

  override def canDefend(used: Card, undefended: Card): Boolean = {
    if (used.beats(undefended)) {
      return true
    }

    if (used.suit == status.trump.get.suit) {
      return undefended.suit != status.trump.get.suit
    }

    false
  }

  override def current: Option[Player] = byTurn(status.turn)

  override def byTurn(turn: Turn): Option[Player] = {
    status.players.find(_.turn == turn)
  }

  override def undo(): Unit = {
    undoManager.undoStep()

    notifySubscribers()
  }

  override def redo(): Unit = {
    undoManager.redoStep()

    notifySubscribers()
  }

  override def load(): Unit = {
    undoManager.doStep(LoadCommand(this, fileIo))

    notifySubscribers()
  }


  override def save(): Unit = {
    undoManager.doStep(SaveCommand(this, fileIo))

    notifySubscribers()
  }

  override def isOver: Boolean = status.players.count(_.turn != Turn.Finished) == 1

  override def unbind(): Unit = {
    fileIo.unbind()
  }
}
