package view.tui.runner

import com.google.inject.Singleton

import java.util.concurrent.ExecutionException
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.io.StdIn

@Singleton
class MultiRunner extends Thread with Runner {

  private var thread: Option[Thread] = None
  private var promise: Option[Promise[String]] = None

  override def run(run: () => Unit): Unit = {
    Thread(() => {
      this.synchronized {
        thread.foreach(_.interrupt())
        promise.filter(!_.isCompleted).foreach(_.tryFailure(new InterruptedException()))
        
        thread = Some(Thread.currentThread())
        promise = None
      }

      try {
        run()
      } catch {
        case _: InterruptedException | _: ExecutionException =>
      }
    }).start()
  }

  override def readLine(prompt: String): String = {
    if (Thread.currentThread().isInterrupted) {
      throw new InterruptedException()
    }

    this.synchronized {
      this.promise = Some(Promise[String]())
    }

    print(prompt)

    Await.result(promise.get.future, Duration.Inf)
  }

  override def run(): Unit = {
    while (true) {
      val line = StdIn.readLine()

      if (line != null) {
        this.synchronized {
          promise.filter(!_.isCompleted).foreach(_.success(line))
        }
      }
    }
  }
}
