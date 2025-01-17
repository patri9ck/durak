package view.tui.runner

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class SingleRunnerSpec extends AnyWordSpec with Matchers {

  "SingleRunner" should {
    "run(() -> Unit)" should {
      "execute the provided function" in {
        var executed = false

        SingleRunner().run(() => executed = true)

        executed should be(true)
      }
    }

    "readLine(String)" should {
      "return the user input from StdIn" in {
        val input = "Test"
        val in = ByteArrayInputStream(input.getBytes)

        Console.withIn(in) {
          SingleRunner().readLine("") should be(input)
        }
      }

      "display the prompt before reading user input" in {
        val input = "Test"
        val in = ByteArrayInputStream(input.getBytes)
        val out = ByteArrayOutputStream()

        Console.withIn(in) {
          Console.withOut(out) {
            SingleRunner().readLine("Prompt: ")
          }
        }

        out.toString should include("Prompt: ")
      }
    }
  }
}
