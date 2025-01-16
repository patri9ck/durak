package model.io

import model.*
import model.status.Status
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.{Failure, Success, Using}

class JsonFileIoSpec extends AnyWordSpec with Matchers {

  "JsonFileIo" should {
    "load" should {
      "load a Status from a JSON file" in {
        val fileIo = JsonFileIo(s"${getClass.getSimpleName}.json")
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))

        Using(PrintWriter(fileIo.fileName)) { writer =>
          writer.write(Json.prettyPrint(Json.toJson(status)))
        }

        val result = fileIo.load

        result should be(a[Success[_]])
        result.get should be(status)

        File(fileIo.fileName).delete()
      }

      "return None for invalid JSON file" in {
        val fileIo = JsonFileIo(s"${getClass.getSimpleName}.json")
        val json = "{ invalid }"

        Using(PrintWriter(fileIo.fileName)) { writer =>
          writer.write(json)
        }

        val result = JsonFileIo(fileIo.fileName).load

        result should be(a[Failure[_]])

        File(fileIo.fileName).delete()
      }

      "handle missing file gracefully when loading" in {
        val result = JsonFileIo(s"${getClass.getSimpleName}.json").load

        result should be(a[Failure[_]])
      }
    }

    "save(Status)" should {
      "save a Status to a JSON file" in {
        val fileIo = JsonFileIo(s"${getClass.getSimpleName}.json")
        val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))

        val result = fileIo.save(status)

        result should be(a[Success[_]])

        Using(Source.fromFile(fileIo.fileName)) { source =>
          val content = source.mkString

          Json.parse(content).as[Status] should be(status)
        }

        File(fileIo.fileName).delete()
      }
    }

    "unbind()" should {
      "do nothing" in {
        noException should be thrownBy JsonFileIo(s"${getClass.getSimpleName}.json").unbind()
      }
    }

    "this()" should {
      "create a JsonFileIo with default file name" in {
        JsonFileIo().fileName should be(JsonFileIo.FileName)
      }
    }
  }
}