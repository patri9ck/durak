package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {

  "Player" should {
    "toString" should {
      "return the player's name" in {
        val player = Player("Player 1", List(), Turn.Watching)

        player.toString should be("Player 1")
      }
    }
  }
}
