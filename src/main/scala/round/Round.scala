package round

class Round(val group: Group, val run: Group => Group, val stop: Group => Boolean, val loser: Group => Player) {
  def start(): Player = {
    if (stop.apply(group)) {
      loser.apply(group)
    }

    Round(run.apply(group), run, stop, loser).start()
  }
}

