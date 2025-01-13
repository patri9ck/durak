package view.tui.runner

class StubRunner(val input: String) extends Runner {
  
    override def run(run: () => Unit): Unit = run.apply()
  
    override def readLine(prompt: String): String = input
}
