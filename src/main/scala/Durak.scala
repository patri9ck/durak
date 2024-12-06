import controller.base.BaseController
import view.Gui
import view.tui.Tui

import scala.util.CommandLineParser

object Durak {
  @main
  def main(step: Boolean): Unit = {
    val controller = BaseController()

    Tui(controller, step).start()
    Gui(controller, step).main(Array.empty)
  }
}


