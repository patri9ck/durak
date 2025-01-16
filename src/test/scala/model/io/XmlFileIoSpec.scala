package model.io

import model.*
import model.status.Status
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.{Failure, Success, Using}
import scala.xml.{PrettyPrinter, XML}

class XmlFileIoSpec extends AnyWordSpec with Matchers {

  "XmlFileIo" should {
    "load" should {
      "load a Status from an XML file" in {
        val fileIo = XmlFileIo(s"${getClass.getSimpleName}.xml")
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))

        Using(new PrintWriter(fileIo.fileName)) { writer =>
          writer.write(new PrettyPrinter(120, 4).format(status.toXml))
        }

        val result = fileIo.load

        result should be(a[Success[_]])
        result.get should be(status)

        File(fileIo.fileName).delete()
      }

      "return None for invalid XML file" in {
        val fileIo = XmlFileIo(s"${getClass.getSimpleName}.xml")
        val xml = "<invalid></xml>"

        Using(new PrintWriter(fileIo.fileName)) { writer =>
          writer.write(xml)
        }

        val result = fileIo.load

        result should be(a[Failure[_]])

        File(fileIo.fileName).delete()
      }

      "handle missing file gracefully when loading" in {
        val fileIo = XmlFileIo(s"${getClass.getSimpleName}.xml")
        val result = fileIo.load

        result should be(a[Failure[_]])
      }
    }

    "save(Status)" should {
      "save a Status to an XML file" in {
        val fileIo = XmlFileIo(s"${getClass.getSimpleName}.xml")
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))

        val result = fileIo.save(status)

        result should be(a[Success[_]])

        Using(Source.fromFile(fileIo.fileName)) { source =>
          val content = XML.loadString(source.mkString)

          Status.fromXml(content) should be(status)
        }

        File(fileIo.fileName).delete()
      }
    }

    "unbind()" should {
      "do nothing" in {
        noException should be thrownBy XmlFileIo(s"${getClass.getSimpleName}.xml").unbind()
      }
    }

    "this()" should {
      "create a XmlFileIo with default file name" in {
        XmlFileIo().fileName should be(XmlFileIo.FileName)
      }
    }
  }
}