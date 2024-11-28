import view.{ViewCreator, ViewType}

import scala.util.CommandLineParser

object Durak {
  var viewCreator: ViewType => ViewCreator = ViewCreator.apply

  @main
  def main(view: String, step: Boolean): Unit = {
    viewCreator.apply(view match {
      case "gui" => ViewType.Gui
      case _ => ViewType.Tui
    }).createView(step).start()
  }
}


