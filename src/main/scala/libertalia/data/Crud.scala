package libertalia.data

import scala.language.reflectiveCalls

import org.scalarelational.column.property._
import org.scalarelational.extra.HikariSupport
import org.scalarelational.mariadb.{MariaDBConfig, MariaDBDatastore}
import org.scalarelational.table.Table
import org.scalarelational.versioning.VersioningSupport
import org.scalarelational.result.QueryResult
import org.scalarelational.mapper._
import org.scalarelational.datatype._
import org.scalarelational.model.SQLDatastore
import org.scalarelational.column._

trait Crud[Id, EntityModel <: {val id: Option[Id]}] {
  val datastore: SQLDatastore
  import datastore._

  def table: Table { val id: Column[Option[Id], Id] }

  /** Map database response to a model. */
  def extractModel(r: QueryResult): EntityModel

  /** Set column values from a given model. */
  def modelToRequest(m: EntityModel): Seq[ColumnValue[_, _]]


  def create(m: EntityModel): Id = withSession { implicit sess =>
    insert(modelToRequest(m): _*).result.asInstanceOf[Id]
  }

  def all: List[EntityModel] = withSession { implicit sess =>
    val q = select(table.*) from table
    q.result.map(extractModel).toList
  }

  def get(id: Id): Option[EntityModel] = withSession { implicit sess =>
    val q = select(table.*) from table where table.id === Some(id)
    q.result.map(extractModel).toList.headOption
  }

  def modify(m: EntityModel): Unit = withSession { implicit sess =>
    (update(modelToRequest(m): _*) where table.id === m.id).result
  }

  def remove(id: Id): Unit = withSession { implicit sess =>
    (delete(table) where table.id === Some(id)).result
  }

  def remove(ids: Seq[Id]): Unit = withSession { implicit sess =>
    (delete(table) where (table.id in ids.map(Some(_)))).result
  }

}