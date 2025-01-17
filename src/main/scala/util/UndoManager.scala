package util

/**
 * Executes commands and keeps an undo and redo stack to undo and redo commands.
 */
class UndoManager {

  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  /**
   * Executes a command and puts it onto the undo stack.
   * @param command command to execute
   */
  def doStep(command: Command): Unit = {
    undoStack = command :: undoStack
    command.doStep()
  }

  /**
   * Undoes the last executed command and puts it onto the redo stack.
   */
  def undoStep(): Unit = {
    undoStack match {
      case Nil =>
      case head :: stack =>
        head.undoStep()

        undoStack = stack
        redoStack = head :: redoStack
    }
  }

  /**
   * Redoes the last undone command and puts it onto the undo stack.
   */
  def redoStep(): Unit = {
    redoStack match {
      case Nil =>
      case head :: stack =>
        head.redoStep()
        redoStack = stack
        undoStack = head :: undoStack
    }
  }
}
