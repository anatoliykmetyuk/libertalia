package libertalia.data

import scala.language.reflectiveCalls

import org.scalarelational.column.property._
import org.scalarelational.extra.HikariSupport
import org.scalarelational.mariadb.{MariaDBConfig, MariaDBDatastore}
import org.scalarelational.table.Table
import org.scalarelational.versioning.VersioningSupport
import org.scalarelational.result.QueryResult
import org.scalarelational.mapper._
import org.scalarelational.model.SQLDatastore
import org.scalarelational.column._

/** Aware that this entity belongs to some other entity. */
trait Possessive[Id, RefId, EntityModel <: {val id: Option[Id]}] { this: Crud[Id, EntityModel] =>
  import datastore._

  def foreignKey: Column[RefId, RefId]

  def allOf(ref: RefId): List[EntityModel] = withSession { implicit sess =>
    val q = select(table.*) from table where foreignKey === ref
    q.result.map(extractModel).toList
  }
}
