package view.tui.runner

import com.google.inject.Singleton

import java.util.concurrent.ExecutionException
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.io.StdIn

@Singleton
class MultiRunner extends Thread with Runner {

  private var threads: Map[Thread, Option[Promise[String]]] = Map.empty

  override def run(run: () => Unit): Unit = {
    Thread(() => {
      threads.synchronized {
        threads.values.filter(promise => promise.isDefined).map(promise => promise.get).foreach(promise => promise.tryFailure(new InterruptedException()))
        threads.keys.foreach(thread => thread.interrupt())
        threads = Map(Thread.currentThread() -> None)
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

    val promise = Promise[String]()

    threads.synchronized {
      threads += (Thread.currentThread() -> Some(promise))
    }

    print(prompt)

    Await.result(promise.future, Duration.Inf)
  }

  override def run(): Unit = {
    while (true) {
      val line = StdIn.readLine()

      if (line != null) {
        threads.synchronized {
          threads.values.filter(promise => promise.isDefined).map(promise => promise.get).filter(promise => !promise.isCompleted).foreach(promise => promise.success(line))
        }
      }
    }
  }
}
