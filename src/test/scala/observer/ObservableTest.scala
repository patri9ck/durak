package observer

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// Ein Mock Observer für Testzwecke
class MockObserver extends Observer {
  var updated = false

  override def update(): Unit = {
    updated = true
  }
}

class ObservableSpec extends AnyWordSpec with Matchers {

  "An Observable" should {

    "allow observers to be added" in {
      val observable = new Observable
      val observer = new MockObserver

      observable.add(observer)
      observable.notifySubscribers()

      observer.updated shouldBe true
    }

    "allow observers to be removed" in {
      val observable = new Observable
      val observer = new MockObserver

      observable.add(observer)
      observable.remove(observer)
      observer.updated = false

      observable.notifySubscribers()

      observer.updated shouldBe false
    }

    "notify all subscribers when notifySubscribers is called" in {
      val observable = new Observable
      val observer1 = new MockObserver
      val observer2 = new MockObserver

      observable.add(observer1)
      observable.add(observer2)

      observable.notifySubscribers()

      observer1.updated shouldBe true
      observer2.updated shouldBe true
    }
  }
}