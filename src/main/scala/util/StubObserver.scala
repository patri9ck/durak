package util

class StubObserver extends Observer {
  var updated = false

  override def update(): Unit = {
    updated = true
  }
}
