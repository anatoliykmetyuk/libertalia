package libertalia.data

import org.scalarelational.column.property._
import org.scalarelational.extra.HikariSupport
import org.scalarelational.mariadb.{MariaDBConfig, MariaDBDatastore}
import org.scalarelational.table.Table
import org.scalarelational.versioning.VersioningSupport
import org.scalarelational.result.QueryResult


object Datastore
  extends MariaDBDatastore(MariaDBConfig(
    "localhost", "libertalia", "root", "", serverTimezone = Some("UTC")
  ))
  with HikariSupport
  with VersioningSupport
  with OrganizationComponent {

  val tables = Seq(
    persistentProperties
  , Organization
  )

  def createOrUpgrade(): Unit = withSession { implicit session =>
    if (jdbcTables.isEmpty) create(tables: _*)
    upgrade()
  }
}
