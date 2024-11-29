import controller.base.BaseController
import view.Tui

import scala.util.CommandLineParser

object Durak {
  @main
  def main(step: Boolean, countdown: Boolean): Unit = {
    val controller = BaseController()

    Tui(controller, step).continue()
  }
}


