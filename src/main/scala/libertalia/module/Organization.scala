package libertalia
package module

import data._
import Datastore.orgs

object Organization extends Module {
  override val name = "org"
  override val processor: ProcessCmd = {
    case Cmd.create :: name :: Nil       => create(name)
    case Nil                             => list()
    case Cmd.update :: id :: name :: Nil => update(id.toInt, name)
    case Cmd.delete :: id :: Nil         => delete(id.toInt)
    case x => s"Unknown command: $x"
  }

  def create(name: String): String = {
    val id = orgs.create(Model.Organization(name))
    reportOrg(id, "Organization created")
  }

  def list(): String = orgs.all.mkString("\n")

  def update(id: Int, name: String) = {
    orgs.modify(Model.Organization(name, Some(id)))
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