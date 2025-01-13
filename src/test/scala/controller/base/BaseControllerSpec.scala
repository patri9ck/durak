package controller.base

import com.google.inject.Guice
import model.*
import model.io.JsonFileIo
import model.status.{MutableStatusBuilder, Status, StatusBuilder}
import module.DurakModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BaseControllerSpec extends AnyWordSpec with Matchers {

  "BaseController" should {
    "chooseAttacking(List[Player], Int)" should {
      "set one player to FirstlyAttacking and the other to Defending when there are two players" in {
        val controller = BaseController(JsonFileIo())
        
        val updatedPlayers = controller.chooseAttacking(List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
        ), 0)

        updatedPlayers.head.turn should be(Turn.FirstlyAttacking)
        updatedPlayers.last.turn should be(Turn.Defending)
      }

      "ignore finished players" in {
        val controller = BaseController(JsonFileIo())
        
        val updatedPlayers = controller.chooseAttacking(List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Finished),
          Player("Player3", Nil, Turn.Watching),
          Player("Player4", Nil, Turn.Watching),
          Player("Player5", Nil, Turn.Watching),
        ), 2)

        updatedPlayers.head.turn should be(Turn.Defending)
        updatedPlayers(1).turn should be(Turn.Finished)
      }

      "set one player to FirstlyAttacking, the player before to Defending and the player before that one to SecondlyAttacking" in {
        val controller = BaseController(JsonFileIo())

        val updatedPlayers = controller.chooseAttacking(List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
          Player("Player3", Nil, Turn.Watching),
        ), 0)

        updatedPlayers.head.turn should be(Turn.FirstlyAttacking)
        updatedPlayers(1).turn should be(Turn.SecondlyAttacking)
        updatedPlayers.last.turn should be(Turn.Defending)
      }


      "set all remaining players to Watching" in {
        val controller = BaseController(JsonFileIo())

        val players = List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
          Player("Player3", Nil, Turn.Watching),
          Player("Player4", Nil, Turn.FirstlyAttacking),
          Player("Player5", Nil, Turn.SecondlyAttacking),
        )
        
        val updatedPlayers = controller.chooseAttacking(players, 2)

        updatedPlayers(3).turn should be(Turn.Watching)
        updatedPlayers.last.turn should be(Turn.Watching)
      }
    }

    "chooseNextAttacking(List[Player], Player)" should {
      "set the player previous in the list to the one specified to FirstlyAttacking" in {
        val controller = BaseController(JsonFileIo())
        
        val previous = Player("Player3", Nil, Turn.Watching)
        
        val updatedPlayers = controller.chooseNextAttacking(List(Player("Player1", Nil, Turn.Watching), Player("Player2", Nil, Turn.Watching), previous), previous)

        updatedPlayers(1).turn should be(Turn.FirstlyAttacking)
      }
    }

    "drawFromStack(StatusBuilder)" should {
      "fill up every player's cards up until the specified amount and take cards from the stack" in {
        val controller = BaseController(JsonFileIo())

        var statusBuilder = MutableStatusBuilder()
          .setPlayers(List(Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking), Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.SecondlyAttacking)))
          .setAmount(6)
          .setStack(List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds)))

        statusBuilder = controller.drawFromStack(statusBuilder)

        statusBuilder.getPlayers.foreach(player => player.cards.length should be(6))
        statusBuilder.getStack should be(empty)
      }

      "start with passed if passed is Some" in {
        val controller = BaseController(JsonFileIo())

        val stack = List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds))
        val passed = Player("Player3", List(Card(Rank.Jack, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Ace, Suit.Clubs)), Turn.Defending)

        var statusBuilder = MutableStatusBuilder()
          .setPlayers(List(Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking), Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.Watching), passed))
          .setAmount(6)
          .setStack(stack)
          .setPassed(passed)

        statusBuilder = controller.drawFromStack(statusBuilder)

        statusBuilder.getPlayers.last.cards(3) should be(stack.head)
      }

      "start with the player that has the turn FirstlyAttacking if passed is None" in {
        val controller = BaseController(JsonFileIo())

        val stack = List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds))

        val statusBuilder = MutableStatusBuilder()
          .setPlayers(List(Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking), Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.Watching)))
          .setAmount(6)
          .setStack(stack)

        controller.drawFromStack(statusBuilder)

        statusBuilder.getPlayers.head.cards(3) should be(stack.head)
      }
    }

    "updatePlayers(List[Player], Player, Player)" should {
      "replace the old player with the updated player" in {
        val controller = BaseController(JsonFileIo())

        val old = Player("Player1", Nil, Turn.Watching)
        val updated = Player("Player1", Nil, Turn.FirstlyAttacking)

        val updatedPlayers = controller.updatePlayers(List(old, Player("Player2", Nil, Turn.Watching), Player("Player3", Nil, Turn.Watching)), old, updated)

        updatedPlayers.head should be(updated)
      }
    }

    "hasFinished(Player, StatusBuilder)" should {
      "return false if the stack is not empty" in {
        val controller = BaseController(JsonFileIo())
        
        val finished = Player("Player1", Nil, Turn.Defending)

        val statusBuilder = MutableStatusBuilder()
          .setStack(List(Card(Rank.Three, Suit.Clubs), Card(Rank.Ten, Suit.Spades)))

        controller.hasFinished(finished, statusBuilder) should be(false)
      }

      "return false if the player still has cards" in {
        val controller = BaseController(JsonFileIo())
        
        val finished = Player("Player1", List(Card(Rank.Ace, Suit.Hearts)), Turn.Defending)
        
        controller.hasFinished(finished, MutableStatusBuilder()) should be(false)
      }

      "return false if the player is Defending and there are undefended cards" in {
        val controller = BaseController(JsonFileIo())
        
        val finished = Player("Player1", Nil, Turn.Defending)

        val statusBuilder = MutableStatusBuilder()
          .setUndefended(List(Card(Rank.Three, Suit.Clubs), Card(Rank.Ten, Suit.Spades)))
        
        controller.hasFinished(finished, statusBuilder) should be(false)
      }

      "return true if the player is Finished" in {
        val controller = BaseController(JsonFileIo())

        val finished = Player("Player1", Nil, Turn.Finished)

        controller.hasFinished(finished, MutableStatusBuilder()) should be(true)
      }

      "return false if the player is Watching" in {
        val controller = BaseController(JsonFileIo())
        
        val finished = Player("Player1", Nil, Turn.Watching)
        
        controller.hasFinished(finished, MutableStatusBuilder()) should be(false)
      }

      "return true if the player is either FirstlyAttacking or SecondlyAttacking, has no cards and the stack is empty" in {
        val controller = BaseController(JsonFileIo())
        
        val finished = Player("Player1", Nil, Turn.FirstlyAttacking)
        
        controller.hasFinished(finished, MutableStatusBuilder()) should be(true)
      }

      "return true if the player is Defending, has no cards, the stack is empty and there are no undefended cards" in {
        val player = Player("Player1", Nil, Turn.Defending)

        controller.status = model.status.Status(List(player), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(JsonFileIo())

        controller.hasFinished(player, MutableStatusBuilder()) should be(true)
      }

    }

    "finish(Player)" should {
      "set the finished player to Finished" in {
        val finished = Player("Player1", Nil, Turn.Defending)

        controller.status = model.status.Status(List(finished,
          Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), Nil, Nil, false, None)
        val statusBuilder = MutableStatusBuilder()
        val controller = BaseController(JsonFileIo())

        controller.finish(finished, statusBuilder)

        statusBuilder.getPlayers.head.turn should be(Turn.Finished)
      }

      "set the next turn to FirstlyAttacking and reset the defended, undefended and used List if the player was Defending" in {
        val defending = Player("Player1", Nil, Turn.Defending)

        controller.status = model.status.Status(List(defending,
          Player("Player2", Nil, Turn.FirstlyAttacking),
          Player("Player3", Nil, Turn.SecondlyAttacking)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), Nil, Nil, false, None)
        val statusBuilder = MutableStatusBuilder()
        val controller = BaseController(JsonFileIo())

        controller.finish(defending, statusBuilder)

        statusBuilder.getTurn should be(Turn.FirstlyAttacking)
        statusBuilder.getPlayers.last.turn should be(Turn.FirstlyAttacking)
        statusBuilder.getDefended should be(empty)
        statusBuilder.getUndefended should be(empty)
        statusBuilder.getUsed should be(empty)
      }

      "set the next turn to Defending if the player was FirstlyAttacking and there is no SecondlyAttacking player or if the player was SecondlyAttacking" in {
        val attacking = Player("Player1", Nil, Turn.FirstlyAttacking)

        controller.status = model.status.Status(List(attacking,
          Player("Player2", Nil, Turn.Defending)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), Nil, Nil, false, None)
        val statusBuilder = MutableStatusBuilder()
        val controller = BaseController(JsonFileIo())

        controller.finish(attacking, statusBuilder)

        statusBuilder.getTurn should be(Turn.Defending)
      }

      "set the next turn to SecondlyAttacking if the player was FirstlyAttacking and there is a SecondlyAttacking player" in {
        val attacking = Player("Player1", Nil, Turn.FirstlyAttacking)

        val controller = BaseController(JsonFileIo())

        controller.status = model.status.Status(List(attacking,
          Player("Player3", Nil, Turn.SecondlyAttacking),
          Player("Player3", Nil, Turn.Defending)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), Nil, Nil, false, None)

        val statusBuilder = MutableStatusBuilder()

        controller.finish(attacking, statusBuilder)

        statusBuilder.getTurn should be(Turn.SecondlyAttacking)
      }
    }

    "canAttack(Card)" should {
      "return true if there are no undefended and no defended cards" in {
        val controller = BaseController(JsonFileIo())

        val card = Card(Rank.Ace, Suit.Spades)

        controller.status = model.status.Status(Nil, Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)

        controller.canAttack(card) should be(true)
      }

      "return true if the used cards contain a card with the same rank" in {
        val controller = BaseController(JsonFileIo())

        val card = Card(Rank.Ace, Suit.Spades)

        controller.status = model.status.Status(Nil, Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, Nil, List(card), false, None)

        controller.canAttack(card) should be(true)
      }

      "return true if the defended cards contain a card with the same rank" in {
        val controller = BaseController(JsonFileIo())

        val card = Card(Rank.Ace, Suit.Spades)

        controller.status = model.status.Status(Nil, Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, List(card), Nil, Nil, false, None)

        controller.canAttack(card) should be(true)
      }

      "return true if the undefended cards contain a card with the same rank" in {
        val controller = BaseController(JsonFileIo())

        val card = Card(Rank.Ace, Suit.Spades)

        controller.status = model.status.Status(Nil, Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, List(card), Nil, false, None)

        controller.canAttack(card) should be(true)
      }
    }

    "deny()" should {

    }

    "pickUp()" should {

    }

    "attack(Card)" should {

    }

    "canDefend(Card, Card)" should {
      "return true if the first card beats the second" in {
        val controller = BaseController(JsonFileIo())

        controller.canDefend(Card(Rank.Ace, Suit.Hearts), Card(Rank.King, Suit.Hearts)) should be(true)
      }

      "return true if the suit of the first card is the trump suit and the second card is not" in {
        val controller = BaseController(JsonFileIo())

        controller.canDefend(Card(Rank.Three, Suit.Spades), Card(Rank.King, Suit.Hearts)) should be(true)
      }

      "return true if the suit of both the first and the second card is the trump suit and the first card beats the second" in {
        val controller = BaseController(JsonFileIo())

        controller.canDefend(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)) should be(true)
      }

      "return false if the first card is not a trump and lower than the second" in {
        val controller = BaseController(JsonFileIo())

        controller.canDefend(Card(Rank.Three, Suit.Hearts), Card(Rank.King, Suit.Hearts)) should be(false)
      }
    }

    "defend(Card, Card)" should {

    }

    "current" should {
      "return the player with the current turn" in {
        val controller = BaseController(JsonFileIo())

        controller.status = model.status.Status(List(Player("Player1", Nil, Turn.FirstlyAttacking), Player("Player2", Nil, Turn.Defending)), Nil, Some(Card(Rank.Ten, Suit.Spades)), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)

        controller.current should be(Some(controller.status.players.head))
      }
    }

    "byTurn(Turn)" should {
      "find a player by his turn" in {
        val controller = BaseController(JsonFileIo())

        controller.status = model.status.Status(List(Player("Player1", Nil, Turn.FirstlyAttacking), Player("Player2", Nil, Turn.Defending),), Nil, Some(Card(Rank.Ten, Suit.Spades)), 3, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)

        controller.byTurn(Turn.FirstlyAttacking) should be(Some(controller.status.players.head))
      }
    }
  }
}