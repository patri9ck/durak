import controller.base.BaseController
import view.Gui
import view.tui.Tui

import scala.util.CommandLineParser

object Durak {
  @main
  def main(): Unit = {
    val controller = BaseController()

    Tui(controller, false).start()
    Gui(controller, false).main(Array.empty)
  }
}


