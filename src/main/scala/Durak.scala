import controller.base.BaseController
import view.Tui

object Durak {
  @main
  def main(): Unit = {
    Tui(BaseController(Tui.createStatus())).start()
  }
}
