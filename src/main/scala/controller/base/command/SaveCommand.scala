package controller.base.command

import controller.base.BaseController
import model.io.FileIo
import scala.util.{Failure, Success}

class SaveCommand(controller: BaseController, fileIo: FileIo) extends MementoCommand(controller) {

  override def execute(): Unit = {
    fileIo.save(controller.status) match {
      case Success(_) => println("Status gespeichert.")
      case Failure(exception) => println(s"Fehler beim Speichern: $exception")
    }
  }
}
