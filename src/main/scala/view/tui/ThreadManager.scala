package view.tui

import java.util.concurrent.ExecutionException
import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

class ThreadManager {

  private val threads = TrieMap[Thread, Promise[String]]()

  def run(run: () => Unit): Unit = {
    Thread(() => {
      threads.values.foreach(promise => promise.tryFailure(InterruptedException()))
      threads.keys.foreach(thread => thread.interrupt())
      threads.clear()

      try {
        run()
      } catch {
        case _: InterruptedException | _: ExecutionException =>
      }
    }).start()
  }

  def addLine(line: String): Unit = {
    threads.values.foreach(promise => promise.success(line))
  }

  def readLine(prompt: String): String = {
    if (Thread.currentThread().isInterrupted) {
      throw InterruptedException()
    }

    val promise = Promise[String]()

    threads.put(Thread.currentThread(), promise)

    println(prompt)

    Await.result(promise.future, Duration.Inf)
  }
}
