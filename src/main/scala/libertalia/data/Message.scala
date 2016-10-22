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

trait MessageComponent { self: Datastore.type =>
  object Message extends Table("Message") {
    val id        = column[Option[Int], Int  ]("id", PrimaryKey, AutoIncrement)
    val title     = column[String            ]("title")
    val text      = column[String            ]("text", MediumText)
    val sender    = column[Int               ]("sender", new ForeignKey(Organization.id))
    val recipient = column[Int               ]("recipient", new ForeignKey(Organization.id))
    val timestamp = column[java.sql.Timestamp]("timestamp")
    val seen      = column[Boolean           ]("seen")
  }

  object msgs extends Crud[Int, Model.Message] with Possessive[Int, Int, Model.Message] {
    override val table      = Message
    override val datastore  = self
    override val foreignKey = table.recipient

    override def extractModel(r: QueryResult): Model.Message = Model.Message(
      r(table.title)
    , r(table.text)
    , r(table.sender)
    , r(table.recipient)
    , r(table.timestamp)
    , r(table.id)
    , r(table.seen))

    override def modelToRequest(m: Model.Message): Seq[ColumnValue[_, _]] = List(
      table.title(m.title)
    , table.text(m.text)
    , table.sender(m.sender)
    , table.recipient(m.recipient)
    , table.timestamp(m.timestamp)
    , table.id(m.id)
    , table.seen(m.seen))
  }
}