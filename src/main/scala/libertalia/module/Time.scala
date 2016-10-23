package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

object Time extends CrudModule[Model.Time, Datastore.times.type] with PossessiveModule[Model.Time, Datastore.times.type] with ShowTime {
  override val name        = "time"
  override val description = "Time in possession of the organizations"
  override val source      = Datastore.times

  def isInt(x: String) = scala.util.Try(x.toInt).isSuccess  // TODO: Create an utility trait

  val instanceProcessor: ProcessCmd = {
    case Cmd.create :: owner :: amount :: reason :: Nil => create { Model.Time(amount.toInt, reason, owner.toInt) }
    case Cmd.create :: owner :: amount           :: Nil => create { Model.Time(amount.toInt, ""    , owner.toInt) }

    case Cmd.update :: id :: amount :: Nil if isInt(amount) => update (id.toInt) { t => t.copy(amount = amount.toInt) }
    case Cmd.update :: id :: reason :: Nil                  => update (id.toInt) { t => t.copy(reason = reason      ) }

    case Cmd.move   :: from :: to :: amount :: reason :: Nil => transfer(from.toInt, to.toInt, amount.toInt, reason                       )
    case Cmd.move   :: from :: to :: amount           :: Nil => transfer(from.toInt, to.toInt, amount.toInt, s"Transfer from $from to $to")
  }

  val instanceHelp = List(
    List(Cmd.create -> false, "owner"-> true, "amount" -> true, "reason" -> true) -> "Grant an organization a certain amount of time"
  , List(Cmd.create -> false, "owner"-> true, "amount" -> true) -> "Grant an organization a certain amount of time"
  , List(Cmd.update -> false, "id"   -> true, "amount" -> true) -> "Change the amount of time with a given id"
  , List(Cmd.update -> false, "id"   -> true, "reason" -> true) -> "Change the reason of a time with a given id"
  , List(Cmd.move   -> false, "from" -> true, "to"     -> true, "amount" -> true, "reason" -> true) -> "Transfer a given amount of time between organizations"
  , List(Cmd.move   -> false, "from" -> true, "to"     -> true, "amount" -> true) -> "Transfer a given amount of time between organizations"
  )

  def transfer(from: Int, to: Int, amount: Int, reason: String) = List(
    create { Model.Time(-amount, reason, from) }
  , create { Model.Time( amount, reason, to  ) }
  ).mkString("\n")
}

trait ShowTime extends ShowEntity[Model.Time] {
  override implicit val showEntity: Show[Model.Time] = Show.show { t =>
    import t._
    s"${id.get} Time: ${timestamp.show}; Amount: ${if (amount >= 0) s"+$amount" else amount}; Owner: $owner; Reason: $reason"
  }
}
