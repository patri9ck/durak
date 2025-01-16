package model

case class SelectableCard(card: Card, var selected: Boolean = false) {

  def this(card: Card) = this(card, false)
}