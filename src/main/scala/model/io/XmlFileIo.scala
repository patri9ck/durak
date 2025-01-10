package model.io

import model.status.Status

import java.io.PrintWriter
import scala.util.{Try, Using}
import scala.xml.{PrettyPrinter, XML}

class XmlFileIo extends FileIo {

  private val FileName: String = "status.xml"

  override def save(status: Status): Try[Unit] = {
    Try {
      Using(new PrintWriter(FileName)) { writer =>
        writer.write(PrettyPrinter(120, 4).format(status.toXml))
      }
    }
  }

  override def load: Try[Option[Status]] = {
    Try {
      Some(Status.fromXml(XML.loadFile(FileName)))
    }
  }

  override def unbind(): Unit = {}
}
