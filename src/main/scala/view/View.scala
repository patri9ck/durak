package view

import observer.Observer

trait View extends Observer {
  def start(): Unit
}
