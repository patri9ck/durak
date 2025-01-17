package model.io

import com.fasterxml.jackson.core.JsonParseException
import model.status.Status
import play.api.libs.json.{JsError, JsSuccess, Json}

import java.io.PrintWriter
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

class JsonFileIo(val fileName: String) extends FileIo {

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
  val FileName: String = "status.json"
}
