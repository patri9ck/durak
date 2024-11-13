package model

import scala.collection.mutable.ListBuffer

case class Group(players: List[Player], stack: List[Card], trump: Card)
