package round

import controller.Group
import model.{Card, Player, Rank, Suit, Turn}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RoundSpec extends AnyWordSpec with Matchers {

  val player1: Player = Player("Alice", List(Card(Rank.Ace, Suit.Spades)), Turn.Watching)
  val player2: Player = Player("Bob", List(Card(Rank.King, Suit.Hearts)), Turn.Watching)
  val initialGroup: Group = Group(List(player1, player2))
  val round = new Round(initialGroup, mockRun, mockStop, mockLoser)

  def mockRun(group: Group): Group = {
    group
  }

  def mockStop(group: Group): Boolean = {
    group.players.exists(_.cards.isEmpty)
  }

  def mockLoser(group: Group): Player = {
    group.players.find(_.cards.isEmpty).getOrElse(group.players.head)
  }

  "A Round" should {
    "start" should {
      "apply the run function on each recursive call" in {
        val modifiedGroup = round.run.apply(initialGroup)
        modifiedGroup shouldEqual initialGroup
      }

      "stop the round when the stop condition is met" in {
        val groupWithEmptyPlayer = Group(List(Player("Charlie", List(), Turn.Watching), player2))
        val stoppingRound = new Round(groupWithEmptyPlayer, mockRun, mockStop, mockLoser)

        val result = stoppingRound.start()
        result.name shouldEqual "Charlie"
      }

      "identify the correct loser according to the loser function" in {
        val groupWithEmptyPlayer = Group(List(Player("Daisy", List(), Turn.Watching), player2))
        val roundWithLoser = new Round(groupWithEmptyPlayer, mockRun, mockStop, mockLoser)

        val result = roundWithLoser.start()
        result.name shouldEqual "Daisy"
      }

      "not terminate if the stopping condition is never met" in {
        val player1 = Player("Alice", List(Card(Rank.Ace, Suit.Spades)), Turn.Watching)
        val player2 = Player("Bob", List(Card(Rank.King, Suit.Hearts)), Turn.Watching)
        val group = Group(List(player1, player2))

        val run: Group => Group = (g: Group) => {
          g
        }

        val stop: Group => Boolean = (_: Group) => false

        val loser: Group => Player = (_: Group) => player1

        val round = new Round(group, run, stop, loser)

        an[StackOverflowError] should be thrownBy round.start()
      }
    }
    }


}

