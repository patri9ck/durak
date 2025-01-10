package model.io

import model.status.Status
import play.api.libs.json.{JsError, JsSuccess, Json}

import java.io.PrintWriter
import scala.io.Source
import scala.util.{Try, Using}

class JsonFileIo extends FileIo {

  private val FileName: String = "status.json"

  override def load: Try[Option[Status]] = {
    Try {
      Using.resource(Source.fromFile(FileName)) { source =>
        Json.parse(source.mkString).validate[Status] match {
          case success: JsSuccess[Status] => Some(success.get)
          case error: JsError =>
            println(error)
            None
        }
      }
    }
  }

  override def save(status: Status): Try[Unit] = {
    Try {
      Using(new PrintWriter(FileName)) { writer =>
        writer.write(Json.stringify(Json.toJson(status)))
      }
    }
  }

  override def unbind(): Unit = {}
}
