package view.tui.runner

import scala.io.StdIn

class SingleRunner extends Runner {

  override def run(run: () => Unit): Unit = run.apply()

  override def readLine(prompt: String): String = StdIn.readLine(prompt)
}
