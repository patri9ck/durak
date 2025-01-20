package util

/**
 * A command executes business logic and can be undone and redone.
 */
trait Command {

  /**
   * Executes the command.
   */
  def doStep(): Unit

  /**
   * Undoes the command.
   */
  def undoStep(): Unit

  /**
   * Redoes the command.
   */
  def redoStep(): Unit
}