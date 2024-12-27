package module

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import controller.Controller
import controller.base.BaseController
import model.io.{FileIo, JsonFileIo}
import model.status.{MutableStatusBuilder, StatusBuilder}
import model.{Card, Player, Turn}
import net.codingwell.scalaguice.ScalaModule
import view.tui.runner.{MultiRunner, Runner}

class DurakModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[Controller].to[BaseController]
    bind[Runner].to[MultiRunner]
    bind[FileIo].to[JsonFileIo]

    bind[StatusBuilder].to[MutableStatusBuilder]

    bindConstant().annotatedWith(Names.named("seconds")).to(3)
    bindConstant().annotatedWith(Names.named("lines")).to(100)

    bind[List[Player]].annotatedWith(Names.named("players")).toInstance(Nil)
    bind[List[Card]].annotatedWith(Names.named("stack")).toInstance(Nil)
    bind[Option[Card]].annotatedWith(Names.named("trump")).toInstance(None)
    bindConstant().annotatedWith(Names.named("amount")).to(0)
    bind[Turn].annotatedWith(Names.named("turn")).toInstance(Turn.Watching)
    bind[List[Card]].annotatedWith(Names.named("defended")).toInstance(Nil)
    bind[List[Card]].annotatedWith(Names.named("undefended")).toInstance(Nil)
    bind[List[Card]].annotatedWith(Names.named("used")).toInstance(Nil)
    bindConstant().annotatedWith(Names.named("denied")).to(false)
    bind[Option[Player]].annotatedWith(Names.named("passed")).toInstance(None)
  }
}
