import controller.base.BaseController
import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BaseControllerSpec extends AnyWordSpec with Matchers {

  "BaseController" should {
    "chooseAttacking(List[Player], Int)" should {
      "set one player to FirstlyAttacking and the other to Defending when there are two players" in {
        val players = List(
          Player("Player1", List(), Turn.Watching),
          Player("Player2", List(), Turn.Watching),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 0)
        updatedPlayers.head.turn should be(Turn.FirstlyAttacking)
        updatedPlayers.last.turn should be(Turn.Defending)
      }

      "ignore finished players" in {
        val players = List(
          Player("Player1", List(), Turn.Watching),
          Player("Player2", List(), Turn.Finished),
          Player("Player3", List(), Turn.Watching),
          Player("Player4", List(), Turn.Watching),
          Player("Player5", List(), Turn.Watching),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 2)

        updatedPlayers.head.turn should be(Turn.Defending)
        updatedPlayers(1).turn should be(Turn.Finished)
      }

      "set one player to FirstlyAttacking, the player before to Defending and the player before that one to SecondlyAttacking" in {
        val players = List(
          Player("Player1", List(), Turn.Watching),
          Player("Player2", List(), Turn.Watching),
          Player("Player3", List(), Turn.Watching),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 0)
        updatedPlayers.head.turn should be(Turn.FirstlyAttacking)
        updatedPlayers(1).turn should be(Turn.SecondlyAttacking)
        updatedPlayers.last.turn should be(Turn.Defending)
      }


      "set all remaining players to Watching" in {
        val players = List(
          Player("Player1", List(), Turn.Watching),
          Player("Player2", List(), Turn.Watching),
          Player("Player3", List(), Turn.Watching),
          Player("Player4", List(), Turn.FirstlyAttacking),
          Player("Player5", List(), Turn.SecondlyAttacking),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 2)

        updatedPlayers(3).turn should be(Turn.Watching)
        updatedPlayers.last.turn should be(Turn.Watching)
      }
    }

    "chooseNextAttacking(List[Player], Player)" should {
      "set the player previous in the list to the one specified to FirstlyAttacking" in {
        val players = List(
          Player("Player1", List(), Turn.Watching),
          Player("Player2", List(), Turn.Watching),
          Player("Player3", List(), Turn.Watching),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseNextAttacking(players, players(2))
        updatedPlayers(1).turn should be(Turn.FirstlyAttacking)
      }
    }

    "chooseAttacking(Player)" should {
      "set the specified player to FirstlyAttacking" in {
        val players = List(
          Player("Player1", List(), Turn.Watching),
          Player("Player2", List(), Turn.Watching),
          Player("Player3", List(), Turn.Watching),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.chooseAttacking(players(1))
        controller.status.group.players(1).turn should be(Turn.FirstlyAttacking)
      }
    }

    "chooseAttacking()" should {
      "set a random player to FirstlyAttacking" in {
        val players = List(
          Player("Player1", List(), Turn.Watching),
          Player("Player2", List(), Turn.Watching),
          Player("Player3", List(), Turn.Watching),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.chooseAttacking()
        controller.status.group.players.exists(_.turn == Turn.FirstlyAttacking) should be(true)
      }
    }

    "drawFromStack()" should {
      "fill up every player's cards up until the specified amount and take cards from the stack" in {
        val amount = 6

        val stack = List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds))

        val players = List(
          Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking),
          Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.SecondlyAttacking),
        )

        val status = Status(Group(players, stack, Card(Rank.Ten, Suit.Spades), amount), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.drawFromStack()

        controller.status.group.players.foreach(player => player.cards.length should be(amount))
        controller.status.group.stack should be(empty)
      }

      "start with passed if passed is Some and stop if the stack is empty" in {
        val amount = 6

        val stack = List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds))

        val passed = Player("Player3", List(Card(Rank.Jack, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Ace, Suit.Clubs)), Turn.Defending)

        val players = List(
          Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking),
          Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.Watching),
          passed
        )

        val status = Status(Group(players, stack, Card(Rank.Ten, Suit.Diamonds), amount), Round(Turn.Watching, List(), List(), List(), false, Some(passed)))
        val controller = BaseController(status)

        controller.drawFromStack()

        controller.status.group.players.last.cards(3) should be(stack.head)
      }

      "start with the player that has the turn FirstlyAttacking if passed is None" in {
        val amount = 6

        val stack = List(Card(Rank.Three, Suit.Clubs), Card(Rank.Four, Suit.Diamonds), Card(Rank.Six, Suit.Spades), Card(Rank.Eight, Suit.Hearts), Card(Rank.Nine, Suit.Clubs), Card(Rank.Ten, Suit.Diamonds))

        val players = List(
          Player("Player1", List(Card(Rank.King, Suit.Clubs), Card(Rank.Seven, Suit.Spades), Card(Rank.Queen, Suit.Hearts)), Turn.FirstlyAttacking),
          Player("Player2", List(Card(Rank.Two, Suit.Spades), Card(Rank.Five, Suit.Hearts), Card(Rank.Ace, Suit.Diamonds)), Turn.Watching),
        )

        val status = Status(Group(players, stack, Card(Rank.Ten, Suit.Diamonds), amount), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.drawFromStack()

        controller.status.group.players.head.cards(3) should be(stack.head)
      }
    }

    "updatePlayers(Player, Player)" should {
      "replace the old player with the updated player" in {
        val old = Player("Player1", List(), Turn.Watching)
        val updated = Player("Player1", List(), Turn.FirstlyAttacking)

        val players = List(
          old,
          Player("Player2", List(), Turn.Watching),
          Player("Player3", List(), Turn.Watching),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        val updatedPlayers = controller.updatePlayers(old, updated)

        updatedPlayers.head should be(updated)
      }
    }

    "hasFinished(Player)" should {
      "return false if the stack is not empty" in {
        val player = Player("Player1", List(), Turn.Defending)

        val status = Status(Group(List(player), List(Card(Rank.Three, Suit.Clubs), Card(Rank.Ten, Suit.Spades)), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.hasFinished(player) should be(false)
      }

      "return false if the player still has cards" in {
        val player = Player("Player1", List(Card(Rank.Ace, Suit.Hearts)), Turn.Defending)

        val status = Status(Group(List(player), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.hasFinished(player) should be(false)
      }

      "return false if the player is Defending and there are undefended cards" in {
        val player = Player("Player1", List(), Turn.Defending)

        val status = Status(Group(List(player), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(Card(Rank.Ace, Suit.Hearts)), List(), false, None))
        val controller = BaseController(status)

        controller.hasFinished(player) should be(false)
      }

      "return true if the player is Watching" in {
        val player = Player("Player1", List(), Turn.Watching)

        val status = Status(Group(List(player), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.hasFinished(player) should be(true)
      }

      "return true if the player is either FirstlyAttacking or SecondlyAttacking, has no cards and the stack is empty" in {
        val player = Player("Player1", List(), Turn.FirstlyAttacking)

        val status = Status(Group(List(player), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(Card(Rank.Ace, Suit.Hearts)), List(), false, None))
        val controller = BaseController(status)

        controller.hasFinished(player) should be(true)
      }

      "return true if the player is Defending, has no cards, the stack is empty and there are no undefended cards" in {
        val player = Player("Player1", List(), Turn.Defending)

        val status = Status(Group(List(player), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.hasFinished(player) should be(true)
      }

    }

    "finish(Player)" should {
      "set the finished player to Finished" in {
        val finished = Player("Player1", List(), Turn.Defending)

        val status = Status(Group(List(finished,
          Player("Player2", List(), Turn.FirstlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), List(), List(), false, None))
        val controller = BaseController(status)

        controller.finish(finished)

        controller.status.group.players.head.turn should be(Turn.Finished)
      }

      "set the next turn to FirstlyAttacking and reset the defended, undefended and used List if the player was Defending" in {
        val defending = Player("Player1", List(), Turn.Defending)

        val status = Status(Group(List(defending,
          Player("Player2", List(), Turn.FirstlyAttacking),
          Player("Player3", List(), Turn.SecondlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), List(), List(), false, None))
        val controller = BaseController(status)

        controller.finish(defending)

        controller.status.round.turn should be(Turn.FirstlyAttacking)
        controller.status.group.players.last.turn should be(Turn.FirstlyAttacking)
        controller.status.round.defended should be(empty)
        controller.status.round.undefended should be(empty)
        controller.status.round.used should be(empty)
      }

      "set the next turn to Defending if the player was FirstlyAttacking and there is no SecondlyAttacking player or if the player was SecondlyAttacking" in {
        val attacking = Player("Player1", List(), Turn.FirstlyAttacking)

        val status = Status(Group(List(attacking,
          Player("Player2", List(), Turn.Defending)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), List(), List(), false, None))
        val controller = BaseController(status)

        controller.finish(attacking)

        controller.status.round.turn should be(Turn.Defending)
      }

      "set the next turn to SecondlyAttacking if the player was FirstlyAttacking and there is a SecondlyAttacking player" in {
        val attacking = Player("Player1", List(), Turn.FirstlyAttacking)

        val status = Status(Group(List(attacking,
          Player("Player3", List(), Turn.SecondlyAttacking),
          Player("Player3", List(), Turn.Defending)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(Card(Rank.Ace, Suit.Hearts)), List(), List(), false, None))
        val controller = BaseController(status)

        controller.finish(attacking)

        controller.status.round.turn should be(Turn.SecondlyAttacking)
      }
    }

    "canAttack(Card)" should {
      "return true if there are no undefended and no defended cards" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val status = Status(Group(List(), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.canAttack(card) should be(true)
      }

      "return true if the used cards contain a card with the same rank" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val status = Status(Group(List(), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(), List(card), false, None))
        val controller = BaseController(status)

        controller.canAttack(card) should be(true)
      }

      "return true if the defended cards contain a card with the same rank" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val status = Status(Group(List(), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(card), List(), List(), false, None))
        val controller = BaseController(status)

        controller.canAttack(card) should be(true)
      }

      "return true if the undefended cards contain a card with the same rank" in {
        val card = Card(Rank.Ace, Suit.Spades)

        val status = Status(Group(List(), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(card), List(), false, None))
        val controller = BaseController(status)

        controller.canAttack(card) should be(true)
      }
    }

    "denied()" should {
      "set the defending player to the attacking player if all cards are defended and all attacking players denied" in {
        val players = List(
          Player("Player1", List(), Turn.SecondlyAttacking),
          Player("Player2", List(), Turn.Defending),
          Player("Player3", List(), Turn.FirstlyAttacking)
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.SecondlyAttacking, List(), List(), List(), true, None))
        val controller = BaseController(status)

        controller.denied()

        controller.status.group.players(1).turn should be(Turn.FirstlyAttacking)
      }

      "set the turn to Defending if both attacking players denied and there are still undefended cards" in {
        val players = List(
          Player("Player1", List(), Turn.SecondlyAttacking),
          Player("Player2", List(), Turn.Defending),
          Player("Player3", List(), Turn.FirstlyAttacking)
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.SecondlyAttacking, List(), List(Card(Rank.Ace, Suit.Spades)), List(), true, None))
        val controller = BaseController(status)

        controller.denied()

        controller.status.round.turn should be(Turn.Defending)
      }

      "set the turn to SecondlyAttacking if there are two attacking players and the first one denied" in {
        val players = List(
          Player("Player1", List(), Turn.SecondlyAttacking),
          Player("Player2", List(), Turn.Defending),
          Player("Player3", List(), Turn.FirstlyAttacking)
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(Card(Rank.Ace, Suit.Spades)), List(), false, None))
        val controller = BaseController(status)

        controller.denied()

        controller.status.round.turn should be(Turn.SecondlyAttacking)
      }
    }

    "pickUp()" should {
      "fill up the defending player's cards with the used, defended and undefended cards" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Seven, Suit.Spades)), Turn.Defending),
          Player("Player2", List(), Turn.FirstlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(Card(Rank.Ace, Suit.Spades)), List(Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades)), List(Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades)), false, None))
        val controller = BaseController(status)

        controller.pickUp()

        controller.status.group.players.head.cards should contain allElementsOf List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades), Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades))
      }

      "set the turn to FirstlyAttacking and reset the defended, undefended and used List" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Seven, Suit.Spades)), Turn.Defending),
          Player("Player2", List(), Turn.FirstlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(Card(Rank.Ace, Suit.Spades)), List(Card(Rank.King, Suit.Spades), Card(Rank.Queen, Suit.Spades)), List(Card(Rank.Jack, Suit.Spades), Card(Rank.Ten, Suit.Spades)), false, None))
        val controller = BaseController(status)

        controller.pickUp()

        controller.status.round.turn should be(Turn.FirstlyAttacking)
        controller.status.round.defended should be(empty)
        controller.status.round.undefended should be(empty)
        controller.status.round.used should be(empty)
      }
    }

    "attack(Card)" should {
      "remove the card from the attacking player and add it to the undefended cards" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades), Card(Rank.Seven, Suit.Spades)), Turn.FirstlyAttacking),
          Player("Player2", List(), Turn.Defending)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.attack(Card(Rank.Ace, Suit.Spades))

        controller.status.group.players.head.cards.size should be(1)
        controller.status.round.undefended should contain(Card(Rank.Ace, Suit.Spades))
      }

      "set the turn to Defending if there is only one attacking player" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.FirstlyAttacking),
          Player("Player2", List(), Turn.Defending)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.attack(Card(Rank.Ace, Suit.Spades))

        controller.status.round.turn should be(Turn.Defending)
      }

      "set the turn to Defending if the attacking player is SecondlyAttacking" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.SecondlyAttacking),
          Player("Player2", List(), Turn.Defending), Player("Player3", List(), Turn.FirstlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.SecondlyAttacking, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.attack(Card(Rank.Ace, Suit.Spades))

        controller.status.round.turn should be(Turn.Defending)
      }

      "set the turn to SecondlyAttacking if the attacking player is FirstlyAttacking" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.SecondlyAttacking),
          Player("Player2", List(), Turn.Defending), Player("Player3", List(), Turn.FirstlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.attack(Card(Rank.Ace, Suit.Spades))

        controller.status.round.turn should be(Turn.SecondlyAttacking)
      }

      "set the attacking player to finished if he finished" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.FirstlyAttacking),
          Player("Player2", List(), Turn.Defending)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.attack(Card(Rank.Ace, Suit.Spades))

        controller.status.group.players.head.turn should be(Turn.Finished)
      }
    }

    "canDefend(Card, Card)" should {
      "return true if the first card beats the second" in {
        val status = Status(Group(List(), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.canDefend(Card(Rank.Ace, Suit.Hearts), Card(Rank.King, Suit.Hearts)) should be(true)
      }

      "return true if the suit of the first card is the trump suit and the second card is not" in {
        val status = Status(Group(List(), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.canDefend(Card(Rank.Three, Suit.Spades), Card(Rank.King, Suit.Hearts)) should be(true)
      }

      "return true if the suit of both the first and the second card is the trump suit and the first card beats the second" in {
        val status = Status(Group(List(), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.canDefend(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades)) should be(true)
      }

      "return false if the first card is not a trump and lower than the second" in {
        val status = Status(Group(List(), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.canDefend(Card(Rank.Three, Suit.Hearts), Card(Rank.King, Suit.Hearts)) should be(false)
      }
    }

    "defend(Card, Card)" should {
      "remove the used card from the defending player and add it to the used cards and take the undefended to the defended" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades), Card(Rank.Seven, Suit.Spades)), Turn.Defending),
          Player("Player2", List(), Turn.FirstlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(), List(Card(Rank.King, Suit.Spades), Card(Rank.Two, Suit.Hearts)), List(), false, None))
        val controller = BaseController(status)

        controller.defend(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))

        controller.status.group.players.head.cards.size should be(1)
        controller.status.round.used should contain(Card(Rank.Ace, Suit.Spades))
        controller.status.round.defended should contain(Card(Rank.King, Suit.Spades))
        controller.status.round.undefended should not contain Card(Rank.King, Suit.Spades)
      }

      "set the turn to FirstlyAttacking" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades), Card(Rank.Seven, Suit.Spades)), Turn.Defending),
          Player("Player2", List(), Turn.FirstlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(), List(Card(Rank.King, Suit.Spades), Card(Rank.Two, Suit.Hearts)), List(), false, None))
        val controller = BaseController(status)

        controller.defend(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))

        controller.status.round.turn should be(Turn.FirstlyAttacking)
      }

      "set the defending player to finished if he finished" in {
        val status = Status(Group(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Defending),
          Player("Player2", List(), Turn.FirstlyAttacking)), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Defending, List(), List(Card(Rank.King, Suit.Spades)), List(), false, None))
        val controller = BaseController(status)

        controller.defend(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Spades))

        controller.status.group.players.head.turn should be(Turn.Finished)
      }
    }

    "getPlayer" should {
      "return the player with the current turn" in {
        val players = List(
          Player("Player1", List(), Turn.FirstlyAttacking),
          Player("Player2", List(), Turn.Defending),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.getPlayer should be(Some(players.head))
      }
    }

    "byTurn(Turn)" should {
      "find a player by his turn" in {
        val status = Status(Group(List(
          Player("Player1", List(), Turn.FirstlyAttacking),
          Player("Player2", List(), Turn.Defending),
        ), List(), Card(Rank.Ten, Suit.Spades), 3), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.byTurn(Turn.FirstlyAttacking) should be(Some(status.group.players.head))
      }
    }
  }
}