package controller.base.command

import controller.base.BaseController
import model.io.JsonFileIo
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayOutputStream, File, PrintStream}
import java.nio.file.Paths

class SaveCommandSpec extends AnyWordSpec with Matchers {

  "execute()" should {
    "print a message on success" in {
      val fileIo = JsonFileIo(s"${getClass.getSimpleName}.json")
      val output = ByteArrayOutputStream()

      Console.withOut(PrintStream(output)) {
        SaveCommand(BaseController(fileIo), fileIo).execute()
      }

      output.toString should be("Status gespeichert.\n")

      File(fileIo.fileName).delete()
    }

    "print a message on an I/O error" in {
      val fileIo = JsonFileIo(Paths.get(s"${getClass.getSimpleName}", s"${getClass.getSimpleName}.json").toString)
      val output = ByteArrayOutputStream()

      Console.withOut(PrintStream(output)) {
        SaveCommand(BaseController(fileIo), fileIo).execute()
      }

      output.toString should startWith("Fehler beim Speichern: java.io.FileNotFoundException: ")
    }
  }
}
