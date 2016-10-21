package libertalia.data

import org.scalarelational.column.property._
import org.scalarelational.extra.HikariSupport
import org.scalarelational.mariadb.{MariaDBConfig, MariaDBDatastore}
import org.scalarelational.table.Table
import org.scalarelational.versioning.VersioningSupport
import org.scalarelational.result.QueryResult
import org.scalarelational.mapper._
import org.scalarelational.datatype._
import org.scalarelational.column._

trait OrganizationComponent { self: Datastore.type =>
  object Organization extends Table("Organization") {
    val id     = column[Option[Int], Int]("id", PrimaryKey, AutoIncrement)
    val parent = column[Option[Int], Int]("parent", new ForeignKey(id))
    val name   = column[String]("name", Unique)
  }

  object orgs extends Crud[Int, Model.Organization] {
    override val table     = Organization
    override val datastore = self

    override def extractModel(r: QueryResult): Model.Organization = Model.Organization(
      r(table.name  )
    , r(table.parent)
    , r(table.id   ))

    override def modelToRequest(m: Model.Organization): Seq[ColumnValue[_, _]] = Seq(
      table.name(m.name)
    , table.parent(m.parent)
    , table.id(m.id)
    )
  }
}