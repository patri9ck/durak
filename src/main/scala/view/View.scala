package view

import util.Observer

trait View extends Observer {
  def start(): Unit
}
