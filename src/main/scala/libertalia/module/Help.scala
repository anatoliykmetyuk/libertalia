package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

object Help extends Module with ShowHelp {
  override val name        = Cmd.help
  override val description = "Information about how to use Libertalia"

  val instanceProcessor: ProcessCmd = {
    case name :: Nil if Module.all.exists(_.name == name) => displayHelp(Module.all.find(_.name == name).get)
    case name :: Nil => s"Unknown module: $name"
  }

  override def instanceHelp = List(
    List("module_name" -> true) -> "Display help for a module with the given name"
  )

  def displayHelp(m: Module) = m.helpData.show
}

trait ShowHelp extends ShowEntity[Module.HelpEntry] {
  override implicit val showEntity: Show[Module.HelpEntry] = Show.show { case (args, description) =>
    val argStr: String = args.map { case (name, isVar) => if (isVar) s"<$name>" else name }.mkString(" ")
    String.format("%-50s - %s", argStr, description)
  }
}
