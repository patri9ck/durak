import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._
import controller.base.BaseController

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
          Player("Player4", List(), Turn.Watching),
          Player("Player5", List(), Turn.Watching),
        )

        val status = Status(Group(players, List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        val updatedPlayers = controller.chooseAttacking(players, 2)
        updatedPlayers.head.turn should be(Turn.Watching)
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
      "return false if the player is not Defending, FirstlyAttacking or SecondlyAttacking" in {
        val player = Player("Player1", List(), Turn.Watching)

        val status = Status(Group(List(player), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.hasFinished(player) should be(false)
      }

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

      "return true if the player is either Defending, FirstlyAttacking or SecondlyAttacking, has no cards and the stack is empty" in {
        val player = Player("Player1", List(), Turn.Defending)

        val status = Status(Group(List(player), List(), Card(Rank.Ten, Suit.Spades), 6), Round(Turn.Watching, List(), List(), List(), false, None))
        val controller = BaseController(status)

        controller.hasFinished(player) should be(true)
      }
    }



    "allow a valid attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.FirstlyAttacking),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )

      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.canAttack(card) should be (true)
    }

    "deny an invalid attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Defending, List(trump), List(), List(), false, None))
      val controller = BaseController(status)

      controller.canAttack(card) should be (false)

      /*
      status.round.defended.isEmpty && status.round.undefended.isEmpty
      || status.round.used.exists(_.rank == card.rank)
      || status.round.defended.exists(_.rank == card.rank)
      || status.round.undefended.exists(_.rank == card.rank)

       */
    }

    "allow a valid defense" in {
      val card1 = Card(Rank.King, Suit.Spades)
      val card2 = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Defending, List(card1), List(), List(), false, None))
      val controller = BaseController(status)

      controller.canDefend(card2, card1) should be (true)
    }

    "deny an invalid defense" in {
      val card1 = Card(Rank.Ace, Suit.Spades)
      val card2 = Card(Rank.Two, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(List(), stack, trump,3 ), Round(Turn.Defending, List(card1), List(), List(), false, None))
      val controller = BaseController(status)

      controller.canDefend(card2, card1) should be (false)
    }

    "handle denied attack" in {
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.SecondlyAttacking),
        Player("Player2", List(), Turn.Defending),
        Player("Player3", List(), Turn.FirstlyAttacking)
      )
      val status = Status(Group(players, stack, trump, 3), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.denied()
      controller.status.round.denied should be (true)
    }

    "handle pick up" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Defending, List(card), List(), List(), false, None))
      val controller = BaseController(status)

      controller.pickUp()
      controller.status.round.undefended should be (empty)
    }

    "handle attack" in {
      val card = Card(Rank.Ace, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.FirstlyAttacking),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.attack(card)
      controller.status.round.undefended should contain (card)
    }

    "handle defense" in {
      val card1 = Card(Rank.Ace, Suit.Spades)
      val card2 = Card(Rank.King, Suit.Spades)
      val players = List(
        Player("Player1", List(), Turn.Defending),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump,3), Round(Turn.Defending, List(card1), List(), List(), false, None))
      val controller = BaseController(status)

      controller.defend(card2, card1)
      controller.status.round.defended should contain (card1)
    }

    "find player by turn" in {
      val players = List(
        Player("Player1", List(), Turn.FirstlyAttacking),
        Player("Player2", List(), Turn.Watching),
        Player("Player3", List(), Turn.Watching)
      )
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts), Card(Rank.Queen, Suit.Diamonds), Card(Rank.Jack, Suit.Clubs))
      val trump = Card(Rank.Ten, Suit.Spades)

      val status = Status(Group(players, stack, trump, 3), Round(Turn.FirstlyAttacking, List(), List(), List(), false, None))
      val controller = BaseController(status)

      controller.byTurn(Turn.FirstlyAttacking) should be(Some(players.head))
    }
  }
}