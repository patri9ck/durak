import com.google.inject.Guice
import model.io.XmlFileIo
import module.DurakModule
import view.gui.Gui
import view.tui.Tui
import view.tui.runner.{MultiRunner, Runner}

object Durak {
  @main
  def main(): Unit = {
    val injector = Guice.createInjector(new DurakModule)

    injector.getInstance(classOf[Runner]) match
      case multiRunner: MultiRunner =>
        multiRunner.start()
      case _ =>

    injector.getInstance(classOf[Tui]).start()
    injector.getInstance(classOf[Gui]).main(Array.empty)
  }
}


