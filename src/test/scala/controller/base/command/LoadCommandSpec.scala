package controller.base.command

import controller.base.BaseController
import model.*
import model.io.JsonFileIo
import model.status.Status
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayOutputStream, File}

class LoadCommandSpec extends AnyWordSpec with Matchers {

  "execute()" should {
    "load the status" in {
      val status = Status(List(Player("Player1", List(Card(Rank.Ace, Suit.Spades)), Turn.Finished)), List(Card(Rank.King, Suit.Hearts)), Some(Card(Rank.Queen, Suit.Diamonds)), 5, Turn.Finished, List(Card(Rank.Ten, Suit.Clubs)), List(Card(Rank.Nine, Suit.Spades)), List(Card(Rank.Eight, Suit.Hearts)), true, Some(Player("Player2", List(Card(Rank.Seven, Suit.Diamonds)), Turn.Watching)))
      val fileIo = JsonFileIo(s"${getClass.getSimpleName}.json")
      val controller = BaseController(fileIo)

      fileIo.save(status)

      LoadCommand(controller, fileIo).execute()

      controller.status should be(status)

      File(fileIo.fileName).delete()
    }

    "print a message on an I/O error" in {
      val fileIo = JsonFileIo(s"${getClass.getSimpleName}.json")
      val controller = BaseController(fileIo)
      val output = ByteArrayOutputStream()

      Console.withOut(output) {
        LoadCommand(controller, fileIo).execute()
      }

      output.toString should startWith("Fehler beim Laden: java.io.FileNotFoundException: ")
      controller.status should be(Status())
    }
  }
}
