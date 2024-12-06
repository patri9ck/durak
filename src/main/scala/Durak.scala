import controller.base.BaseController
import view.Tui

import scala.io.StdIn
import scala.util.CommandLineParser

object Durak {
  @main
  def main(step: Boolean): Unit = {
    val controller = BaseController()

    val tui = Tui(controller, step)

    tui.update()

    while (true) {
      tui.addLine(StdIn.readLine())
    }
  }
}


