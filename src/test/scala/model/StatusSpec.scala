package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StatusSpec extends AnyWordSpec with Matchers {

  "Status" should {

    "createStatus(Int, List[String])" should {
      "create a status with the correct amount of players and cards" in {
        val names = List("Player1", "Player2")
        val amount = 6

        val status = Status.createStatus(amount, names)

        status.players should have length names.length
        all(status.players.map(_.cards)) should have length amount
        status.amount should be(amount)
      }

      "assign a random trump card" in {
        val names = List("Player1", "Player2")
        val amount = 6

        val status = Status.createStatus(amount, names)

        Rank.values should contain(status.trump.rank)
        Suit.values should contain(status.trump.suit)
      }

      "initialize the round with the correct turn and empty lists" in {
        val names = List("Player1", "Player2")
        val amount = 6

        val status = Status.createStatus(amount, names)

        status.turn should be(Turn.Watching)
        status.defended should be(empty)
        status.undefended should be(empty)
        status.used should be(empty)
        status.passed should be(None)
        status.denied should be(false)
      }
    }
  }
}