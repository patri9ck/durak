package util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UndoManagerSpec extends AnyWordSpec with Matchers {

  class MockCommand extends Command {
    var doCount = 0
    var undoCount = 0
    var redoCount = 0

    override def doStep(): Unit = doCount += 1

    override def undoStep(): Unit = undoCount += 1

    override def redoStep(): Unit = redoCount += 1
  }

  "UndoManager" should {
    "doStep(Command)" should {
      "should execute the command and add it to the undo stack" in {
        val undoManager = UndoManager()
        val command = MockCommand()

        undoManager.doStep(command)

        command.doCount should be(1)
      }
    }

    "undoStep()" should {
      "should undo the last command and move it to the redo stack" in {
        val undoManager = UndoManager()
        val command = MockCommand()

        undoManager.doStep(command)
        undoManager.undoStep()

        command.undoCount should be(1)
      }

      "do nothing if the undo stack is empty" in {
        val undoManager = new UndoManager()

        noException should be thrownBy undoManager.undoStep()
      }
    }

    "redoStep()" should {
      "should redo the last undone command and move it back to the undo stack" in {
        val undoManager = UndoManager()
        val command = MockCommand()

        undoManager.doStep(command)
        undoManager.undoStep()
        undoManager.redoStep()

        command.redoCount should be(1)
      }

      "do nothing if the redo stack is empty" in {
        val undoManager = new UndoManager()

        noException should be thrownBy undoManager.redoStep()
      }
    }
  }
}
