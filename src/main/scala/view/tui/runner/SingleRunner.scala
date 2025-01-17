package view.tui.runner

import scala.io.StdIn

/**
 * A simple implementation of [[view.tui.runner.Runner]] reading simply from standard input single-threaded.
 */
class SingleRunner extends Runner {

  override def run(run: () => Unit): Unit = run.apply()

  override def readLine(prompt: String): String = StdIn.readLine(prompt)
}
