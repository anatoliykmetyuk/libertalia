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
    case Cmd.list   :: cfg               :: Nil  => listVerb(cfg.toList)
  }
  override val processor = organizationProcessor orElse crudProcessor

  def charToCount(c: Char, o: Int): Int = c match {
    case 'd' => Datastore.docs.ownedByCount(o)
    case 'm' => Datastore.msgs.unreadCount(o)
  }

  def listVerbModel(cfg: List[Char]): List[Meta[Model.Organization]] = source.all.map { o =>
    val numbers: List[Int] = cfg.map(charToCount(_, o.id.get))
    o -> numbers
  }

  def listVerb(cfg: List[Char]): String =  listVerbModel(cfg).show

}

trait ShowOrganization extends ShowEntity[Model.Organization] {
  import cats.Id
  import simulacrum._

  type H[F[_], A] = Map[F[Model.Organization], A]
  type HAny[F[_]] = H[F, Any]
  type Meta[A] = (A, List[Int])

  val identStep = 4  // For hierarchies


  @typeclass trait Extract[F[_]] { def get[A](x: F[A]): A }
  import Extract.ops._

  implicit val extractMeta: Extract[Meta] = new Extract[Meta] { override def get[A](x: Meta[A]) = x._1 }
  implicit val extractId: Extract[Id] = new Extract[Id] { override def get[A](a: Id[A]) = a }


  override implicit val showEntity: Show[Model.Organization] = Show.show { o => s"${o.id.get} ${o.name}" }
  
  override implicit val showList: Show[List[Model.Organization]] = showListF[Id]

  implicit def showListF[F[_]](implicit s: Show[F[Model.Organization]], c: Extract[F]): Show[List[F[Model.Organization]]] = Show.show { os =>
    val topLevelIds: Set[Int] = os.filter(_.get.parent.isEmpty).map(_.get.id.get).toSet
    showHierarchy[F]().show(toHierarchy(os, topLevelIds))
  }

  implicit def showMeta[A: Show]: Show[Meta[A]] = Show.show { case (t, meta) =>
    s"${t.show} ${meta.mkString(" ")}"
  }

  implicit def showHierarchy[F[_]](ident: Int = 0)(implicit s: Show[F[Model.Organization]]): Show[HAny[F]] = Show.show { h =>
    val hr: H[F, HAny[F]] = h.mapValues(_.asInstanceOf[HAny[F]])

    hr.toList.map { case (org, children) =>
      val orgStr      = " " * ident + org.show
      val childrenStr = showHierarchy[F](ident + identStep).show(children)
      if (!childrenStr.isEmpty) s"$orgStr\n$childrenStr" else s"$orgStr"
    }.mkString("\n")
  }

  def toHierarchy[F[_]: Extract](os: List[F[Model.Organization]], topLevelIds: Set[Int]): HAny[F] = {
    val topLevelOrgs: List[F[Model.Organization]]                    = os.filter { o => topLevelIds(o.get.id.get) }
    val children    : Map [Option[Int], List[F[Model.Organization]]] = os.groupBy(_.get.parent)
    val remaining   : List[F[Model.Organization]]                    = os diff topLevelOrgs

    topLevelOrgs.map { o =>
      val oChildrenIds: Set[Int] = children.get(o.get.id).toList.flatten.map(_.get.id.get).toSet
      o -> toHierarchy(remaining, oChildrenIds)
    }.toMap
  }
}
