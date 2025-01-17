package controller.base.command

import controller.base.BaseController
import model.io.FileIo

import scala.util.{Failure, Success}

class LoadCommand(controller: BaseController, fileIo: FileIo) extends MementoCommand(controller) {

  override def execute(): Unit = {
    fileIo.load match {
      case Success(status) => controller.status = status
      case Failure(exception) => println(s"Fehler beim Laden: $exception")
    }
  }
}
