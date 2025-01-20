package model.io

import model.status.Status

import scala.util.Try

/**
 * Loads and saves the status to a file.
 */
trait FileIo {

  /**
   * Loads the status from a file.
   * @return the loaded status or an exception
   */
  def load: Try[Status]

  /**
   * Saves the status to a file.
   * @param status the status to save
   * @return nothing or an exception
   */
  def save(status: Status): Try[Unit]

  /**
   * Frees any locked resources.
   */
  def unbind(): Unit
}
