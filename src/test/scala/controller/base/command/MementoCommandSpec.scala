package controller.base.command

import controller.base.BaseController
import model.*
import model.io.JsonFileIo
import model.status.Status
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MementoCommandSpec extends AnyWordSpec with Matchers {

  class MockMementoCommand(controller: BaseController) extends MementoCommand(controller) {
    override def execute(): Unit = {}
  }

  "MementoCommand" should {
    "doStep()" should {
      "save the current status in memento" in {
        val controller = BaseController(JsonFileIo())
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))

        val command = MockMementoCommand(controller)

        controller.status = status

        command.doStep()

        command.memento should be(status)
      }
    }

    "undoStep() and redoStep()" should {
      "set the status to memento and save the old status" in {
        val controller = BaseController(JsonFileIo())
        val oldStatus = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))
        val newStatus = Status()

        val command = MockMementoCommand(controller)

        command.memento = oldStatus
        controller.status = newStatus

        command.undoStep()

        controller.status should be(oldStatus)
        command.memento should be(newStatus)

        command.redoStep()

        controller.status should be(newStatus)
        command.memento should be(oldStatus)
      }
    }
  }

}
