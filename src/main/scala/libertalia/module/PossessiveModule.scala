package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

trait PossessiveModule[EntityModel <: {val id: Option[Int]}, Source <: Possessive[Int, Int, EntityModel]] extends Module { this: CrudModule[EntityModel, Source] with ShowEntity[EntityModel] =>
  val possessiveProcessor: ProcessCmd = {
    case Cmd.of :: id :: Nil => source.allOf(id.toInt).show
  }
}
