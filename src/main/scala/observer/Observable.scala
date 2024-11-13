package observer

class Observable {
  private var subscribers: Vector[Observer] = Vector()
  
  def add(observer: Observer): Unit = subscribers = subscribers:+observer
  
  def remove(observer: Observer): Unit = subscribers = subscribers.filterNot(subscriber => observer == subscriber)
  
  def notifySubscribers(): Unit = subscribers.foreach(subscriber => subscriber.update())
}
