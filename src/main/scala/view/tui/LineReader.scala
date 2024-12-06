package view.tui

import scala.io.StdIn

class LineReader(threadManager: ThreadManager) extends Thread {
  
  override def run(): Unit = {
    while (true) {
      threadManager.addLine(StdIn.readLine())
    }
  }
}
