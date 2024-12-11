import controller.base.BaseController
import view.Gui
import view.tui.Tui

import scala.util.CommandLineParser

object Durak {
  @main
  def main(controllable: Boolean): Unit = {
    val controller = BaseController()

    Tui(controller, controllable).start()
    Gui(controller, controllable).main(Array.empty)
  }
}


