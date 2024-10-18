package round

class Round(val group: Group, val run: Group => Group, val stop: Group => Boolean, val loser: Group => Player) {
  def start(): Player = {
    val newGroup = run.apply(group)

    if (stop.apply(newGroup)) {
      return loser.apply(newGroup)
    }

    Round(newGroup, run, stop, loser).start()
  }
}

