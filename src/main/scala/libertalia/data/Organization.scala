package libertalia.data

import org.scalarelational.column.property._
import org.scalarelational.extra.HikariSupport
import org.scalarelational.mariadb.{MariaDBConfig, MariaDBDatastore}
import org.scalarelational.table.Table
import org.scalarelational.versioning.VersioningSupport
import org.scalarelational.result.QueryResult
import org.scalarelational.mapper._
import org.scalarelational.datatype._

trait OrganizationComponent { this: Datastore.type =>
  object Organization extends Table("Organization") {
    val id     = column[Option[Int], Int]("id", PrimaryKey, AutoIncrement)
    val parent = column[Option[Int], Int]("parent", new ForeignKey(id))
    val name   = column[String]("name", Unique)
  }

  object orgs {
    def make(r: QueryResult): Model.Organization =
      Model.Organization(r(Organization.name), r(Organization.parent), r(Organization.id))

    def create(org: Model.Organization): Int = withSession { implicit sess =>
      insert(Organization.name(org.name), Organization.parent(org.parent)).result
    }

    def all: List[Model.Organization] = withSession { implicit sess =>
      val q = select(Organization.*) from Organization
      q.result.map(make).toList
    }

    def get(id: Int): Option[Model.Organization] = withSession { implicit sess =>
      val q = select(Organization.*) from Organization where Organization.id === Some(id)
      q.result.map(make).toList.headOption
    }

    def modify(org: Model.Organization): Unit = withSession { implicit sess =>
      (update(Organization.name(org.name), Organization.parent(org.parent)) where Organization.id === org.id).result
    }

    def remove(id: Int): Unit = withSession { implicit sess =>
      (delete(Organization) where Organization.id === Some(id)).result
    }
  }

}