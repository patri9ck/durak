package model.io

import model.status.Status
import play.api.libs.json.{JsError, JsSuccess, Json}

import java.io.PrintWriter
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

/**
 * Implements [[model.io.FileIo]] using JSON.
 * @param fileName the name of the file to read from and write to
 */
class JsonFileIo(val fileName: String) extends FileIo {

  /**
   * Uses [[JsonFileIo.FileName]] as the file name.
   */
  def this() = this(JsonFileIo.FileName)

  override def load: Try[Status] = {
    Try {
      Using.resource(Source.fromFile(fileName)) { source =>
        Json.parse(source.mkString).as[Status]
      }
    }
  }

  override def save(status: Status): Try[Unit] = {
    Using(PrintWriter(fileName)) { writer =>
      writer.write(Json.prettyPrint(Json.toJson(status)))
    }
  }

  override def unbind(): Unit = {}
}

object JsonFileIo {
  /**
   * The default file name.
   */
  val FileName: String = "status.json"
}
