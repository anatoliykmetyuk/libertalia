package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

trait PossessiveModule[EntityModel <: {val id: Option[Int]}, Source <: Possessive[Int, Int, EntityModel]] extends Module { this: CrudModule[EntityModel, Source] with ShowEntity[EntityModel] =>
  val possessiveProcessor: ProcessCmd = {
    case Cmd.of :: id :: Nil => source.ownedBy(id.toInt).show
  }

  override def helpData = List(
    (List(("of", false), ("id", true)), "Display all the entities of this module that belong to an organization with a given id")
  ) ++ super.helpData

  override def processor = possessiveProcessor orElse super.processor
}
