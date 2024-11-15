package model

enum Turn(val name: String) {
  case Defending extends Turn("Verteidigen")
  case FirstlyAttacking extends Turn("Primär Angreifen")
  case SecondlyAttacking extends Turn("Sekundär Angreifen")
  case Watching extends Turn("Zuschauen")
  case Finished extends Turn("Fertig")
}