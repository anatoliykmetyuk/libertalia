package libertalia.data

import org.scalarelational.column.property._
import org.scalarelational.extra.HikariSupport
import org.scalarelational.mariadb.{MariaDBConfig, MariaDBDatastore}
import org.scalarelational.table.Table
import org.scalarelational.versioning.VersioningSupport
import org.scalarelational.result.QueryResult
import org.scalarelational.mapper._

trait OrganizationComponent { this: Datastore.type =>
  object Organization extends MappedTable[Model.Organization]("Organization") {
    val id   = column[Option[Int], Int]("id", PrimaryKey, AutoIncrement)
    val name = column[String]("name", Unique)

    override def query = q.to[Model.Organization](Organization)
  }

  def makeOrg(r: QueryResult): Model.Organization =
    Model.Organization(r(Organization.name), r(Organization.id))

  def createOrg(org: Model.Organization): Int = withSession { implicit sess =>
    org.insert.result.id
  }

  def allOrgs: List[Model.Organization] = withSession { implicit sess =>
    val q = select(Organization.*) from Organization
    q.to[Model.Organization](Organization).result.map(makeOrg).toList
  }

  def updateOrg(org: Model.Organization): Unit = withSession { implicit sess =>
    org.update.result
  }

  def deleteOrg(org: Model.Organization): Unit = withSession { implicit sess =>
    org.delete.result
  }
}