package model.io

import model.status.Status

import java.io.PrintWriter
import scala.util.{Try, Using}
import scala.xml.{PrettyPrinter, XML}

class XmlFileIo(var fileName: String) extends FileIo {

  def this() = this(XmlFileIo.FileName)

  override def save(status: Status): Try[Unit] = {
    Using(new PrintWriter(fileName)) { writer =>
      writer.write(PrettyPrinter(120, 4).format(status.toXml))
    }
  }

  override def load: Try[Status] = {
    Try {
      Status.fromXml(XML.loadFile(fileName))
    }
  }

  override def unbind(): Unit = {}
}

object XmlFileIo {
  val FileName: String = "status.xml"
}
