package round

enum Status {
  case Started
}

class Round(mappedPlayers: Map[Player, Turn]) {
  def run(round: Map[Player, Turn] => Map[Player, Turn]): Map[Player, Turn] = {
    val newlyMappedPlayers = round.apply(mappedPlayers)

    if (newlyMappedPlayers.values.forall(_ == Turn.Finished)) {
      return newlyMappedPlayers
    }
    
    Round(newlyMappedPlayers).run(round)
  }
}

