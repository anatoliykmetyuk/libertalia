package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

object Organization extends CrudModule[Model.Organization, Datastore.orgs.type] with ShowOrganization {
  override val name   = "org"
  override val source = Datastore.orgs
  val organizationProcessor: ProcessCmd = {
    case Cmd.create ::          name     :: Nil  => create           { Model.Organization(name, None)              }
    case Cmd.create :: owner :: name     :: Nil  => create           { Model.Organization(name, Some(owner.toInt)) }
    case Cmd.update :: id    :: name     :: args => update(id.toInt) { _.copy(name = name, parent = args.headOption.map(_.toInt))     }
    case Cmd.move   :: id    :: newOwner :: Nil  => update(id.toInt) { _.copy(             parent = Some(newOwner.toInt))             }
  }
  
  override val processor = organizationProcessor orElse crudProcessor
}

trait ShowOrganization extends ShowEntity[Model.Organization] {
  type H[A] = Map[Model.Organization, A]
  type Hierarchy = H[Any]

  val identStep = 4  // For hierarchies

  override implicit val showEntity: Show[Model.Organization] = Show.show { o => s"${o.id.get} ${o.name}" }
  override implicit val showList  : Show[List[Model.Organization]] = Show.show { os =>
    val topLevelIds: Set[Int] = os.filter(_.parent.isEmpty).map(_.id.get).toSet
    showHierarchy().show(toHierarchy(os, topLevelIds))
  }

  def showHierarchy(ident: Int = 0): Show[Hierarchy] = Show.show { h =>
    val hr: H[Hierarchy] = h.mapValues(_.asInstanceOf[Hierarchy])

    hr.toList.map { case (org, children) =>
      val orgStr      = " " * ident + org.show
      val childrenStr = showHierarchy(ident + identStep).show(children)
      if (!childrenStr.isEmpty) s"$orgStr\n$childrenStr" else s"$orgStr"
    }.mkString("\n")
  }

  def toHierarchy(os: List[Model.Organization], topLevelIds: Set[Int]): Hierarchy = {
    val topLevelOrgs: List[Model.Organization]                    = os.filter { o => topLevelIds(o.id.get) }
    val children    : Map [Option[Int], List[Model.Organization]] = os.groupBy(_.parent)
    val remaining   : List[Model.Organization]                    = os diff topLevelOrgs

    topLevelOrgs.map { o =>
      val oChildrenIds: Set[Int] = children.get(o.id).toList.flatten.map(_.id.get).toSet
      o -> toHierarchy(remaining, oChildrenIds)
    }.toMap
  }
}
