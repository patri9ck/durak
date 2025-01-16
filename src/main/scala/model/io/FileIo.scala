package model.io

import model.status.Status

import scala.util.Try

trait FileIo {

  def load: Try[Status]

  def save(status: Status): Try[Unit]

  def unbind(): Unit
}
