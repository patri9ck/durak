import view.{ViewCreator, ViewType}

object Durak {
  @main
  def main(): Unit = {
    ViewCreator.apply(ViewType.Tui).createView().start()
  }
}
