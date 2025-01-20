package view.tui.runner

/**
 * Used for [[view.tui.Tui]] to read from the standard input or another source and to run code in a controlled environment
 */
trait Runner {

  /**
   * Runs the given function in a controlled environment.
   * @param run the function to run
   */
  def run(run: () => Unit): Unit

  /**
   * Reads user input
   * @param prompt the prompt to display
   * @return the line read from standard input or another source
   */
  def readLine(prompt: String): String
}
