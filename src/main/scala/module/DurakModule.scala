package module

import com.google.inject.AbstractModule
import controller.Controller
import controller.base.BaseController
import model.io.{FileIo, JsonFileIo, XmlFileIo}
import model.status.{MutableStatusBuilder, StatusBuilder}
import net.codingwell.scalaguice.ScalaModule
import view.tui.runner.{MultiRunner, Runner}

class DurakModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[Controller].to[BaseController]
    bind[Runner].to[MultiRunner]
    bind[FileIo].to[JsonFileIo]
    bind[StatusBuilder].to[MutableStatusBuilder]
  }
}
