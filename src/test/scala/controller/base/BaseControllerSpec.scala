package controller.base

import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BaseControllerSpec extends AnyWordSpec with Matchers {

  "BaseController" should {
    "chooseAttacking(List[Player], Int)" should {
      "set one player to FirstlyAttacking and the other to Defending when there are two players" in {
        val players = List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 0)
        updatedPlayers.head.turn should be(Turn.FirstlyAttacking)
        updatedPlayers.last.turn should be(Turn.Defending)
      }

      "ignore finished players" in {
        val players = List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Finished),
          Player("Player3", Nil, Turn.Watching),
          Player("Player4", Nil, Turn.Watching),
          Player("Player5", Nil, Turn.Watching),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 2)

        updatedPlayers.head.turn should be(Turn.Defending)
        updatedPlayers(1).turn should be(Turn.Finished)
      }

      "set one player to FirstlyAttacking, the player before to Defending and the player before that one to SecondlyAttacking" in {
        val players = List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
          Player("Player3", Nil, Turn.Watching),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 0)
        updatedPlayers.head.turn should be(Turn.FirstlyAttacking)
        updatedPlayers(1).turn should be(Turn.SecondlyAttacking)
        updatedPlayers.last.turn should be(Turn.Defending)
      }


      "set all remaining players to Watching" in {
        val players = List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
          Player("Player3", Nil, Turn.Watching),
          Player("Player4", Nil, Turn.FirstlyAttacking),
          Player("Player5", Nil, Turn.SecondlyAttacking),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 2)

        updatedPlayers(3).turn should be(Turn.Watching)
        updatedPlayers.last.turn should be(Turn.Watching)
      }
    }

    "chooseNextAttacking(List[Player], Player)" should {
      "set the player previous in the list to the one specified to FirstlyAttacking" in {
        val players = List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
          Player("Player3", Nil, Turn.Watching),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseNextAttacking(players, players(2))
        updatedPlayers(1).turn should be(Turn.FirstlyAttacking)
      }
    }

    "chooseAttacking(Player)" should {
      "set the specified player to FirstlyAttacking" in {
        val players = List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
          Player("Player3", Nil, Turn.Watching),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.chooseAttacking(players(1))
        controller.status.players(1).turn should be(Turn.FirstlyAttacking)
      }
    }

    "chooseAttacking()" should {
      "set a random player to FirstlyAttacking" in {
        val players = List(
          Player("Player1", Nil, Turn.Watching),
          Player("Player2", Nil, Turn.Watching),
          Player("Player3", Nil, Turn.Watching),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.chooseAttacking()
        controller.status.players.exists(_.turn == Turn.FirstlyAttacking) should be(true)
      }
    }

    "drawFromStack()" should {
      "fill up every player's cards up until the specified amount and take cards from the stack" in {
        val stack = List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds))

        val players = List(
          Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking),
          Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.SecondlyAttacking),
        )

        val status = Status(players, stack, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val statusBuilder = StatusBuilder.create(status)
        val controller = BaseController(status)

        controller.drawFromStack(statusBuilder)

        statusBuilder.getPlayers.foreach(player => player.cards.length should be(6))
        statusBuilder.getStack should be(empty)
      }

      "start with passed if passed is Some and stop if the stack is empty" in {
        val stack = List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds))

        val passed = Player("Player3", List(Card(Rank.Jack, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Ace, Suit.Clubs)), Turn.Defending)

        val players = List(
          Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking),
          Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.Watching),
          passed
        )

        val status = Status(players, stack, Card(Rank.Ten, Suit.Diamonds), 6, Turn.Watching, Nil, Nil, Nil, false, Some(passed))
        val statusBuilder = StatusBuilder.create(status)
        val controller = BaseController(status)

        controller.drawFromStack(statusBuilder)

        statusBuilder.getPlayers.last.cards(3) should be(stack.head)
      }

      "start with the player that has the turn FirstlyAttacking if passed is None" in {
        val stack = List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds))

        val players = List(
          Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking),
          Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.Watching),
        )

        val status = Status(players, stack, Card(Rank.Ten, Suit.Diamonds), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val statusBuilder = StatusBuilder.create(status)
        val controller = BaseController(status)

        controller.drawFromStack(statusBuilder)

        statusBuilder.getPlayers.head.cards(3) should be(stack.head)
      }
    }

    "updatePlayers(Player, Player)" should {
      "replace the old player with the updated player" in {
        val old = Player("Player1", Nil, Turn.Watching)
        val updated = Player("Player1", Nil, Turn.FirstlyAttacking)

        val players = List(
          old,
          Player("Player2", Nil, Turn.Watching),
          Player("Player3", Nil, Turn.Watching),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        val updatedPlayers = controller.updatePlayers(status.players, old, updated)

        updatedPlayers.head should be(updated)
      }
    }

    "hasFinished(Player, StatusBuilder)" should {
      "return false if the stack is not empty" in {
        val player = Player("Player1", Nil, Turn.Defending)

        val status = Status(List(player), List(Card(Rank.Three, Suit.Clubs), Card(Rank.Ten, Suit.Spades)), Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.hasFinished(player, StatusBuilder.create(status)) should be(false)
      }

      "return false if the player still has cards" in {
        val player = Player("Player1", List(Card(Rank.Ace, Suit.Hearts)), Turn.Defending)

        val status = Status(List(player), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.hasFinished(player, StatusBuilder.create(status)) should be(false)
      }

      "return false if the player is Defending and there are undefended cards" in {
        val player = Player("Player1", Nil, Turn.Defending)

        val status = Status(List(player), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, List(Card(Rank.Ace, Suit.Hearts)), Nil, false, None)
        val controller = BaseController(status)

        controller.hasFinished(player, StatusBuilder.create(status)) should be(false)
      }

      "return true if the player is Watching" in {
        val player = Player("Player1", Nil, Turn.Watching)

        val status = Status(List(player), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.hasFinished(player, StatusBuilder.create(status)) should be(true)
      }

      "return true if the player is either FirstlyAttacking or SecondlyAttacking, has no cards and the stack is empty" in {
        val player = Player("Player1", Nil, Turn.FirstlyAttacking)

        val status = Status(List(player), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, List(Card(Rank.Ace, Suit.Hearts)), Nil, false, None)
        val controller = BaseController(status)

        controller.hasFinished(player, StatusBuilder.create(status)) should be(true)
      }

      "return true if the player is Defending, has no cards, the stack is empty and there are no undefended cards" in {
        val player = Player("Player1", Nil, Turn.Defending)

        val status = Status(List(player), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Watching, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.hasFinished(player, StatusBuilder.create(status)) should be(true)
      }

    }

    "finish(Player)" should {
      "set the finished player to Finished" in {
        val finished = Player("Player1", Nil, Turn.Defending)

        val status = Status(List(finished,
          Player("Player2", Nil, Turn.FirstlyAttacking)), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), Nil, Nil, false, None)
        val statusBuilder = StatusBuilder.create(status)
        val controller = BaseController(status)

        controller.finish(finished, statusBuilder)

        statusBuilder.getPlayers.head.turn should be(Turn.Finished)
      }

      "set the next turn to FirstlyAttacking and reset the defended, undefended and used List if the player was Defending" in {
        val defending = Player("Player1", Nil, Turn.Defending)

        val status = Status(List(defending,
          Player("Player2", Nil, Turn.FirstlyAttacking),
          Player("Player3", Nil, Turn.SecondlyAttacking)), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), Nil, Nil, false, None)
        val statusBuilder = StatusBuilder.create(status)
        val controller = BaseController(status)

        controller.finish(defending, statusBuilder)

        statusBuilder.getTurn should be(Turn.FirstlyAttacking)
        statusBuilder.getPlayers.last.turn should be(Turn.FirstlyAttacking)
        statusBuilder.getDefended should be(empty)
        statusBuilder.getUndefended should be(empty)
        statusBuilder.getUsed should be(empty)
      }

      "set the next turn to Defending if the player was FirstlyAttacking and there is no SecondlyAttacking player or if the player was SecondlyAttacking" in {
        val attacking = Player("Player1", Nil, Turn.FirstlyAttacking)

        val status = Status(List(attacking,
          Player("Player2", Nil, Turn.Defending)), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), Nil, Nil, false, None)
        val statusBuilder = StatusBuilder.create(status)
        val controller = BaseController(status)

        controller.finish(attacking, statusBuilder)

        statusBuilder.getTurn should be(Turn.Defending)
      }

      "set the next turn to SecondlyAttacking if the player was FirstlyAttacking and there is a SecondlyAttacking player" in {
        val attacking = Player("Player1", Nil, Turn.FirstlyAttacking)

        val status = Status(List(attacking,
          Player("Player3", Nil, Turn.SecondlyAttacking),
          Player("Player3", Nil, Turn.Defending)), Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), Nil, Nil, false, None)
        val statusBuilder = StatusBuilder.create(status)
        val controller = BaseController(status)

        controller.finish(attacking, statusBuilder)

        statusBuilder.getTurn should be(Turn.SecondlyAttacking)
      }
    }

    "canAttack(Card)" should {
      "return true if there are no undefended and no defended cards" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val status = Status(Nil, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.canAttack(card) should be(true)
      }

      "return true if the used cards contain a card with the same rank" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val status = Status(Nil, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.FirstlyAttacking, Nil, Nil, List(card), false, None)
        val controller = BaseController(status)

        controller.canAttack(card) should be(true)
      }

      "return true if the defended cards contain a card with the same rank" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val status = Status(Nil, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.FirstlyAttacking, List(card), Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.canAttack(card) should be(true)
      }

      "return true if the undefended cards contain a card with the same rank" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val status = Status(Nil, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.FirstlyAttacking, Nil, List(card), Nil, false, None)
        val controller = BaseController(status)

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
        val status = Status(Nil, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Defending, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.canDefend(Card(Rank.Ace, Suit.Hearts), Card(Rank.King, Suit.Hearts)) should be(true)
      }

      "return true if the suit of the first card is the trump suit and the second card is not" in {
        val status = Status(Nil, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Defending, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.canDefend(Card(Rank.Three, Suit.Spades), Card(Rank.King, Suit.Hearts)) should be(true)
      }

      "return true if the suit of both the first and the second card is the trump suit and the first card beats the second" in {
        val status = Status(Nil, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Defending, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.canDefend(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)) should be(true)
      }

      "return false if the first card is not a trump and lower than the second" in {
        val status = Status(Nil, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.Defending, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.canDefend(Card(Rank.Three, Suit.Hearts), Card(Rank.King, Suit.Hearts)) should be(false)
      }
    }

    "defend(Card, Card)" should {

    }

    "getPlayer" should {
      "return the player with the current turn" in {
        val players = List(
          Player("Player1", Nil, Turn.FirstlyAttacking),
          Player("Player2", Nil, Turn.Defending),
        )

        val status = Status(players, Nil, Card(Rank.Ten, Suit.Spades), 6, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.current should be(Some(players.head))
      }
    }

    "byTurn(Turn)" should {
      "find a player by his turn" in {
        val status = Status(List(
          Player("Player1", Nil, Turn.FirstlyAttacking),
          Player("Player2", Nil, Turn.Defending),
        ), Nil, Card(Rank.Ten, Suit.Spades), 3, Turn.FirstlyAttacking, Nil, Nil, Nil, false, None)
        val controller = BaseController(status)

        controller.byTurn(Turn.FirstlyAttacking) should be(Some(status.players.head))
      }
    }
  }
}