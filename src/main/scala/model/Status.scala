package model

case class Status(group: Group, round: Round)

object Status {
  def createStatus(amount: Int, names: List[String]): Status = {
    Status(
      Group.createGroup(amount, names),
      Round.createRound,
    )
  }
}
