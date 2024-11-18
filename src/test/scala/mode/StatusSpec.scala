import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StatusSpec extends AnyWordSpec with Matchers {

  "Status" should {

    "createStatus(Int, List[String])" should {

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
}