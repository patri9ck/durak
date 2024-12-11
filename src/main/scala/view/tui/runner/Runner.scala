package view.tui.runner

trait Runner {

  def run(run: () => Unit): Unit

  def readLine(prompt: String): String
}
