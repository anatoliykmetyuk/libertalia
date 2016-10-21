package libertalia
package module

import data._
import Datastore.orgs

object Organization extends Module with ShowHierarchy {
  override val name = "org"
  override val processor: ProcessCmd = {
    case Cmd.create :: name :: args       => create(name, args)
    case Nil                              => list()
    case id :: Nil                        => read(id.toInt)
    case Cmd.update :: id :: name :: args => update(id.toInt, name, args)
    case Cmd.delete :: ids                => delete(ids.map(_.toInt))
    case Cmd.move   :: id :: where :: Nil => move(id.toInt, where.toInt)
    case x => s"Unknown command: $x"
  }

  def create(name: String, args: List[String]): String = {
    val maybeParent = args.headOption.map(_.toInt)
    val id = orgs.create(Model.Organization(name.filter(_ != '\\'), maybeParent))
    reportOrg(id, "Organization created")
  }

  def list(): String = {
    val allOrgs: List[Model.Organization] = orgs.all
    val topLevelIds: Set[Int] = allOrgs.filter(_.parent.isEmpty).map(_.id.get).toSet
    showHierarchy(toHierarchy(allOrgs, topLevelIds))
  }

  def read(id: Int): String = orgs.get(id).toString

  def update(id: Int, name: String, args: List[String]) = {
    val maybeParent = args.headOption.map(_.toInt)
    orgs.modify(Model.Organization(name, maybeParent, Some(id)))
    reportOrg(id, "Organization updated")
  }

  def move(id: Int, where: Int): String = {
    val org = orgs.get(id).get
    update(id, org.name, List(where.toString))
  }

  def delete(ids: List[Int]) = {
    orgs.remove(ids)
    s"Organizations deleted: $ids"
  }


  def reportOrg(id: Int, msg: String): String = {
    val org: Model.Organization = orgs.get(id).get
    s"$msg: ${showOrg(org)}"
  }
}

trait ShowHierarchy {
  type H[A] = Map[Model.Organization, A]
  type Hierarchy = H[Any]

  val identStep = 4  // For hierarchies

  def toHierarchy(os: List[Model.Organization], topLevelIds: Set[Int]): Hierarchy = {
    val topLevelOrgs: List[Model.Organization]                    = os.filter { o => topLevelIds(o.id.get) }
    val children    : Map [Option[Int], List[Model.Organization]] = os.groupBy(_.parent)
    val remaining   : List[Model.Organization]                    = os diff topLevelOrgs

    topLevelOrgs.map { o =>
      val oChildrenIds: Set[Int] = children.get(o.id).toList.flatten.map(_.id.get).toSet
      o -> toHierarchy(remaining, oChildrenIds)
    }.toMap
  }

  def showHierarchy(h: Hierarchy, ident: Int = 0): String = {
    val hr: H[Hierarchy] = h.mapValues(_.asInstanceOf[Hierarchy])

    hr.toList.map { case (org, children) =>
      val orgStr = " " * ident + showOrg(org)
      val childrenStr = showHierarchy(children, ident + identStep)
      if (!childrenStr.isEmpty) s"$orgStr\n$childrenStr" else s"$orgStr"
    }.mkString("\n")
  }

  def showOrg(org: Model.Organization): String = {
    import org._
    s"${id.get} $name"
  }
}
