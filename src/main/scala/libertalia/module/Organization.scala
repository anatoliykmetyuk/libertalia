package libertalia
package module

import data._
import Datastore.orgs

object Organization extends Module {
  override val name = "org"
  override val processor: ProcessCmd = {
    case Cmd.create :: name :: args       => create(name, args)
    case Nil                              => list()
    case id :: Nil                        => read(id.toInt)
    case Cmd.update :: id :: name :: args => update(id.toInt, name, args)
    case Cmd.delete :: id :: Nil          => delete(id.toInt)
    case x => s"Unknown command: $x"
  }

  def create(name: String, args: List[String]): String = {
    val maybeParent = args.headOption.map(_.toInt)
    val id = orgs.create(Model.Organization(name, maybeParent))
    reportOrg(id, "Organization created")
  }

  def list(): String = orgs.all.mkString("\n")

  def read(id: Int): String = orgs.get(id).toString

  def update(id: Int, name: String, args: List[String]) = {
    val maybeParent = args.headOption.map(_.toInt)
    orgs.modify(Model.Organization(name, maybeParent, Some(id)))
    reportOrg(id, "Organization updated")
  }

  def delete(id: Int) = {
    orgs.remove(id)
    s"Organization deleted: $id"
  }


  def reportOrg(id: Int, msg: String): String = {
    val org: Model.Organization = orgs.get(id).get
    s"$msg: $org"
  }
}