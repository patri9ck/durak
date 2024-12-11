import controller.base.BaseController
import view.Gui
import view.tui.Tui
import view.tui.runner.MultiRunner

import scala.util.CommandLineParser

object Durak {
  @main
  def main(): Unit = {
    val controller = BaseController()
    
    val runner = MultiRunner()
    
    runner.start()

    Tui(controller, runner).start()
    Gui(controller).main(Array.empty)
  }
}


