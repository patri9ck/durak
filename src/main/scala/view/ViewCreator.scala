package view

import view.tui.Tui

trait ViewCreator {
  def createView(): View
}

object ViewCreator {
  def apply(viewType: ViewType): ViewCreator = viewType match {
    case ViewType.Tui => Tui
    case ViewType.Gui => null
  }
}