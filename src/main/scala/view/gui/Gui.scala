package view.gui

import controller.Controller
import view.{View, ViewCreator}

class Gui(val controller: Controller) extends View {

  controller.add(this)

  override def update(): Unit = {

  }
  
  override def start(): Unit = {
    
  }
}

object Gui extends ViewCreator {
  override def createView(): View = new Gui(null)
}
