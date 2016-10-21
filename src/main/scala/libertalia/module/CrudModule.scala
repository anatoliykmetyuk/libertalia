package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

trait CrudModule[EntityModel <: {val id: Option[Int]}, Source <: Crud[Int, EntityModel]] extends Module { this: ShowEntity[EntityModel] =>
  val crudProcessor: ProcessCmd = {
    case Nil               => list()
    case id :: Nil         => read(id.toInt)
    case Cmd.delete :: ids => delete(ids.map(_.toInt))

    case x => s"Unknown command: $x"
  }

  val source: Source


  def create(e: EntityModel): String = {
    val id = source.create(e)
    report(id, "Created")
  }

  def list(): String = source.all.show

  def read(id: Int): String = source.get(id).get.show

  def update(id: Int)(mod: EntityModel => EntityModel): String = {
    val existing = source.get(id).get
    source.modify(mod(existing))
    report(id, "Updated")
  }

  def delete(ids: List[Int]): String = {
    source.remove(ids)
    s"Deleted: $ids"
  }

  def report(id: Int, msg: String): String = {
    val e: EntityModel = source.get(id).get
    s"$msg: ${e.show}"
  }
}

trait ShowEntity[EntityModel] {
  implicit def showEntity: Show[EntityModel]
  implicit def showList  : Show[List[EntityModel]]
}
