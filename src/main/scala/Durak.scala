import controller.base.BaseController
import view.Tui

object Durak {
  @main
  def main(): Unit = {
    val tui = Tui(BaseController())

    tui.start()
  }

}
