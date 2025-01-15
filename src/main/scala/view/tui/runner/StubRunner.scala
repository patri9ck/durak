package view.tui.runner

class StubRunner(var inputs: List[String]) extends Runner {

  override def run(run: () => Unit): Unit = run.apply()

  override def readLine(prompt: String): String = {
    val input = inputs.head

    inputs = inputs.tail

    input
  }
}
