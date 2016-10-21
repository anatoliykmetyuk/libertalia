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

trait DocumentComponent { self: Datastore.type =>
  object Document extends Table("Document") {
    val id    = column[Option[Int], Int]("id", PrimaryKey, AutoIncrement)
    val owner = column[Int]("owner", new ForeignKey(Organization.id))
    val name  = column[String]("name")
    val text  = column[String]("text")
  }

  object docs extends Crud[Int, Model.Document] {
    override val table     = Document
    override val datastore = self

    override def extractModel(r: QueryResult): Model.Document = Model.Document(
      r(table.name)
    , r(table.text)
    , r(table.owner)
    , r(table.id))

    override def modelToRequest(m: Model.Document): Seq[ColumnValue[_, _]] = List(
      table.owner(m.owner)
    , table.name (m.name )
    , table.text (m.text )
    , table.id   (m.id   ))
  }

}