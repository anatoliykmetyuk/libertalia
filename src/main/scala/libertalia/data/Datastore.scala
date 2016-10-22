package libertalia.data

import org.scalarelational._
import org.scalarelational.column.property._
import org.scalarelational.extra.HikariSupport
import org.scalarelational.mariadb.{MariaDBConfig, MariaDBDatastore}
import org.scalarelational.table.Table
import org.scalarelational.versioning._
import org.scalarelational.result.QueryResult
import org.scalarelational.datatype.{SimpleDataType, SQLType}


object Datastore
  extends MariaDBDatastore(MariaDBConfig(
    "localhost", "libertalia", "root", "", serverTimezone = Some("UTC")
  ))
  with HikariSupport
  with VersioningSupport
  with OrganizationComponent
  with DocumentComponent
  with MessageComponent {

  val MediumText = new SimpleDataType[String](java.sql.Types.VARCHAR, SQLType("MEDIUMTEXT"))
    
  DatastoreVersions.versions.foreach(register)

  def createOrUpgrade(): Unit = withSession { implicit session =>
    if (jdbcTables.isEmpty) create(persistentProperties)
    upgrade()
  }
}

object DatastoreVersions {
  import Datastore._

  def ver(n: Int)(diff: Session => Unit) = new UpgradableVersion {
    override def version = n
    override def runOnNewDatabase = true
    override def upgrade(implicit session: Session) = diff(session)
  }

  val versions = Seq(
    ver(1) { implicit sess => create(Organization) }
  , ver(2) { implicit sess => create(Document    ) }
  , ver(3) { implicit sess => create(Message     ) }
  )
}
