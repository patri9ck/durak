package view.tui

import java.util.concurrent.ExecutionException
import scala.collection.immutable.Map
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

class ThreadManager {

  private var threads: Map[Thread, Option[Promise[String]]] = Map.empty

  def run(run: () => Unit): Thread = {
    val thread = new Thread(() => {
      this.synchronized {
        threads.values.filter(promise => promise.isDefined).map(promise => promise.get).foreach(promise => promise.tryFailure(new InterruptedException()))
        threads.keys.foreach(thread => thread.interrupt())
        threads = Map(Thread.currentThread() -> None)
      }

      try {
        run()
      } catch {
        case _: InterruptedException | _: ExecutionException =>
      }
    })
    
    thread.start()
    thread
  }

  def addLine(line: String): Unit = {
    this.synchronized {
      threads.values.filter(promise => promise.isDefined).map(promise => promise.get).foreach(promise => promise.success(line))
    }
  }

  def readLine(prompt: String): String = {
    if (Thread.currentThread().isInterrupted) {
      throw new InterruptedException()
    }

    val promise = Promise[String]()

    this.synchronized {
      threads += (Thread.currentThread() -> Some(promise))
    }

    println(prompt)

    Await.result(promise.future, Duration.Inf)
  }
}
