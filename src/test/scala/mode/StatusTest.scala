import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model._

class StatusSpec extends AnyWordSpec with Matchers {

  "A Status" should {

    "correctly initialize with given group and round" in {
      val players = List(Player("Player1", List(), Turn.Watching), Player("Player2", List(), Turn.Watching))
      val stack = List(Card(Rank.Ace, Suit.Spades), Card(Rank.King, Suit.Hearts))
      val trump = Card(Rank.Queen, Suit.Diamonds)
      val amount = 6
      val group = Group(players, stack, trump, amount)
      val round = Round(Turn.FirstlyAttacking, List(), List(), List(), false, None)

      val status = Status(group, round)

      status.group should be(group)
      status.round should be(round)
    }

    "create a status with the correct amount of players and cards" in {
      val names = List("Player1", "Player2")
      val amount = 6

      val status = Status.createStatus(amount, names)

      status.group.players should have length names.length
      all(status.group.players.map(_.cards)) should have length amount
      status.group.amount should be(amount)
    }

    "assign a random trump card" in {
      val names = List("Player1", "Player2")
      val amount = 6

      val status = Status.createStatus(amount, names)

      Rank.values should contain(status.group.trump.rank)
      Suit.values should contain(status.group.trump.suit)
    }

    "initialize the round with the correct turn and empty lists" in {
      val names = List("Player1", "Player2")
      val amount = 6

      val status = Status.createStatus(amount, names)

      status.round.turn should be(Turn.Watching)
      status.round.defended should be(empty)
      status.round.undefended should be(empty)
      status.round.used should be(empty)
      status.round.passed should be(None)
      status.round.denied should be(false)
    }
  }
}