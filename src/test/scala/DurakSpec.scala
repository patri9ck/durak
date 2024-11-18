import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayInputStream, OutputStream, PrintStream}

class DurakSpec extends AnyWordSpec with Matchers {

  "Durak" should {
    "main()" should {
      "execute without errors" in {
        val input = new ByteArrayInputStream("2\n6\nplayer1\nplayer2\nw\nw\nw\nz\n".getBytes)

        Console.withIn(input) {
          Console.withOut(new PrintStream(OutputStream.nullOutputStream())) {
            try {
              Durak.main()
            } catch {
              case e: Exception => input.available() should be(0)
            }
          }
        }
      }
    }
  }
}