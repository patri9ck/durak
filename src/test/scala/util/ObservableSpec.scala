package util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ObservableSpec extends AnyWordSpec with Matchers {

  class MockObserver extends Observer {
    var updated = false

    override def update(): Unit = {
      updated = true
    }
  }

  "Observer" should {
    "add(Observer)" should {
      "add an observer to the list of observers" in {
        val observable = Observable()
        val observer = MockObserver()

        observable.add(observer)
        observable.notifySubscribers()

        observer.updated shouldBe true
      }
    }

    "remove(Observer)" should {
      "remove an observer from the list of observers" in {
        val observable = Observable()
        val observer = MockObserver()

        observable.add(observer)
        observable.remove(observer)
        observer.updated = false

        observable.notifySubscribers()

        observer.updated shouldBe false
      }
    }

    "notifySubscribers()" should {
      "notify all observers" in {
        val observable = Observable()
        val observer1 = MockObserver()
        val observer2 = MockObserver()

        observable.add(observer1)
        observable.add(observer2)

        observable.notifySubscribers()

        observer1.updated shouldBe true
        observer2.updated shouldBe true
      }
    }
  }
}