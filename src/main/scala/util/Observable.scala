package util

/**
 * An observable can be observed by observers by notifying them upon changes.
 */
class Observable {

  private var subscribers: List[Observer] = List()

  /**
   * Adds an observer.
   * @param observer the observer to add
   */
  def add(observer: Observer): Unit = subscribers = subscribers :+ observer

  /**
   * Removes an observer.
   * @param observer the observer to remove
   */
  def remove(observer: Observer): Unit = subscribers = subscribers.filterNot(subscriber => observer == subscriber)

  /**
   * Notifies all added observers.
   */
  def notifySubscribers(): Unit = subscribers.foreach(subscriber => subscriber.update())
}
