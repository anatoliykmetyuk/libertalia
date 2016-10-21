package libertalia
package module

import data._
import Datastore.orgs

import cats._
import cats.syntax.all._
import cats.instances.all._

object Document extends Module {
  override val name = "doc"
  override val processor: ProcessCmd = {
    case Cmd.create :: name :: Nil        => create(name)
    case Nil                              => list()
    case id :: Nil                        => read(id.toInt)
    case Cmd.update :: id :: Nil          => update(id.toInt)
    case Cmd.delete :: ids                => delete(ids.map(_.toInt))
    case Cmd.move   :: id :: where :: Nil => move(id.toInt, where.toInt)
    case x => s"Unknown command: $x"
  }

  val editor = new util.FileEditor(config.editorPath)

  def create(name: String): String = {
    val docBody = editor.create()
    s"Received document:\n$docBody"
  }

  def list(): String = ???

  def read(id: Int): String = ???

  def update(id: Int) = ???

  def move(id: Int, where: Int): String = ???

  def delete(ids: List[Int]) = ???
}
