import controller.base.BaseController
import view.Gui
import view.tui.Tui

import scala.util.CommandLineParser

object Durak {
  @main
  def main(): Unit = {
    val controller = BaseController()

    Tui(controller).start()
    Gui(controller).main(Array.empty)
  }
}


