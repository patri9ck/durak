package util

/**
 * An observer can be updated by observables.
 */
trait Observer {

  /**
   * Will be called when an observable notifies its observers.
   */
  def update(): Unit
}
