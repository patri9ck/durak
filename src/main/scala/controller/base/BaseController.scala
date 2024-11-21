package controller.base

import controller.Controller
import model.*
import observer.Observable

import scala.annotation.tailrec
import scala.util.Random

case class BaseController(var status: Status) extends Controller {

  def chooseAttacking(players: List[Player], index: Int): List[Player] = {
    require(index >= 0)
    require(index < players.size)

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
  
  def chooseNextAttacking(players: List[Player], previous: Player): List[Player] = {
    chooseAttacking(players, (players.indexOf(previous) - 1 + players.size) % players.size)
  }

  override def chooseAttacking(): Unit = {
    require(status.players.nonEmpty)

    chooseAttacking(Random.shuffle(status.players).head)
  }

  override def chooseAttacking(attacking: Player): Unit = {
    status = StatusBuilder.create(status)
      .setPlayers(chooseAttacking(status.players, status.players.indexWhere(_ == attacking)))
      .setTurn(Turn.FirstlyAttacking)
      .status

    notifySubscribers()
  }

  override def denied(): Unit = {
    requireAttack()
    requireTurn(Turn.FirstlyAttacking)

    val attacking = getPlayer.get

    val statusBuilder = StatusBuilder.create(status)

    if (status.denied || byTurn(Turn.SecondlyAttacking).isEmpty) {
      if (status.undefended.isEmpty) {
        drawFromStack(statusBuilder)

        statusBuilder
          .setPlayers(chooseNextAttacking(status.players, byTurn(Turn.FirstlyAttacking).get))
          .setTurn(Turn.FirstlyAttacking)
          .resetRound
      } else {
        statusBuilder
          .setTurn(Turn.Defending)
      }
    } else {
      statusBuilder
        .setTurn(Turn.SecondlyAttacking)
        .setDenied(true)
    }

    status = statusBuilder.status
    
    notifySubscribers()
  }

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

  override def getPlayer: Option[Player] = byTurn(status.turn)
  
  override def byTurn(turn: Turn): Option[Player] = {
    status.players.find(_.turn == turn)
  }

  private def requireTurn(turn: Turn): Unit = {
    require(byTurn(turn).nonEmpty)
  }

  private def requireAttack(): Unit = {
    require(status.turn == Turn.FirstlyAttacking || status.turn == Turn.SecondlyAttacking)
  }

  override def pickUp(): Unit = {
    requireDefend()
    requireTurn(status.turn)

    val defending = getPlayer.get

    val statusBuilder = StatusBuilder.create(status)

    drawFromStack(statusBuilder)

    val updated = defending.copy(cards = defending.cards ++ status.used ++ status.defended ++ status.undefended)

    status = statusBuilder
      .setPlayers(chooseNextAttacking(updatePlayers(status.players, defending, updated), updated))
      .resetRound
      .status

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
  
  private def requireDefend(): Unit = {
    require(status.turn == Turn.Defending)
  }

  override def attack(card: Card): Unit = {
    requireAttack()
    requireTurn(status.turn)
    require(canAttack(card))

    val attacking = getPlayer.get

    val updated = attacking.copy(cards = attacking.cards.filterNot(_ == card))

    val statusBuilder = StatusBuilder.create(status)
      .setPlayers(updatePlayers(status.players, attacking, updated))
      .setUndefended(card :: status.undefended)
      .setDenied(false)

    if (updated.turn == Turn.FirstlyAttacking && byTurn(Turn.SecondlyAttacking).isEmpty || updated.turn == Turn.SecondlyAttacking) {
      statusBuilder.setTurn(Turn.Defending)
    } else {
      statusBuilder.setTurn(Turn.SecondlyAttacking)
    }

    if (hasFinished(updated, statusBuilder)) {
      finish(updated, statusBuilder)
    }

    status = statusBuilder.status

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
    requireDefend()
    requireTurn(Turn.Defending)
    require(canDefend(used, undefended))

    val defending = byTurn(Turn.Defending).get

    val updated = defending.copy(cards = defending.cards.filterNot(_ == used))

    val statusBuilder = StatusBuilder.create(status)
      .setPlayers(updatePlayers(status.players, defending, updated))
      .setUndefended(status.undefended.filterNot(_ == undefended))
      .setDefended(undefended :: status.defended)
      .setUsed(used :: status.used)
      .setTurn(Turn.FirstlyAttacking)

    if (hasFinished(updated, statusBuilder)) {
      finish(updated, statusBuilder)
    }

    status = statusBuilder.status

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
}





