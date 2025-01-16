package view.tui.runner

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.ByteArrayInputStream
import java.util.concurrent.ExecutionException
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

class MultiRunnerSpec extends AnyWordSpec with Matchers {

  "MultiRunner" should {
    "run(() => Unit)" should {
      "stop all reads" in {
        val runner = MultiRunner()

        val promise = Promise[Boolean]()

        runner.run(() => {
          try {
            runner.readLine("")
          } catch {
            case _: InterruptedException | _: ExecutionException =>
          }

          promise.success(true)
        })

        Thread.sleep(100)

        runner.run(() => {})

        Await.result(promise.future, Duration.Inf) should be(true)
      }

      "execute the passed function" in {
        val runner = MultiRunner()

        val promise = Promise[Boolean]()

        runner.run(() => promise.success(true))

        Await.result(promise.future, Duration.Inf) should be(true)
      }

      "catch an InterruptedException or ExecutionException" in {
        val runner = MultiRunner()

        noException should be thrownBy runner.run(() => throw InterruptedException())
        noException should be thrownBy runner.run(() => throw ExecutionException(null))
      }
    }

    "readLine(String)" should {
      "throw an InterruptedException if the current thread is interrupted" in {
        val runner = MultiRunner()

        val input = "Test"
        val in = ByteArrayInputStream(input.getBytes)
        val promise = Promise[Exception]()

        runner.run(() => {
          Thread.currentThread().interrupt()

          try {
            runner.readLine("")
          } catch {
            case e: Exception => promise.success(e)
          }
        })

        Await.result(promise.future, Duration.Inf) should be(a[InterruptedException])
      }
    }

    "readLine(String) and run()" should {
      "read from StdIn and notify all threads with the input" in {
        val runner = MultiRunner()

        val input = "Test"
        val in = ByteArrayInputStream(input.getBytes)
        val promise = Promise[String]()

        runner.run(() => {
          promise.success(runner.readLine(""))
        })

        Thread.sleep(100)

        Thread(() => {
          Console.withIn(in) {
            runner.run()
          }
        }).start()

        Await.result(promise.future, Duration.Inf) should be(input)
      }
    }
  }
}
