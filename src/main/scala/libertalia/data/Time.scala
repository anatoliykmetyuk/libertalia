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

trait TimeComponent { self: Datastore.type =>
  object Time extends Table("Time") {
    val id     = column[Option[Int], Int     ]("id", PrimaryKey, AutoIncrement)
    val amount = column[Int                  ]("amount")
    val reason = column[String               ]("reason")
    val owner  = column[Int                  ]("owner", new ForeignKey(Organization.id))
    val timestamp = column[java.sql.Timestamp]("timestamp")
  }

  object times extends Crud[Int, Model.Time] with Possessive[Int, Int, Model.Time] {
    override val table      = Time
    override val datastore  = self
    override val foreignKey = table.owner

    override def extractModel(r: QueryResult): Model.Time = Model.Time(
      r(table.amount)
    , r(table.reason)
    , r(table.owner)
    , r(table.timestamp)
    , r(table.id))

    override def modelToRequest(m: Model.Time): Seq[ColumnValue[_, _]] = List(
      table.amount(m.amount)
    , table.reason(m.reason)
    , table.owner(m.owner)
    , table.timestamp(m.timestamp)
    , table.id(m.id))

    def ownedBySum(ref: Int): Int = withSession { implicit sess =>
      val q = select(Sum(table.amount) as "foo") from table where foreignKey === ref
      val h = q.result.head.values.head.value  // TODO: Bug in ScalaRelational, where q.converted.head is sometimes `null`
      if (h != null) h.toString.toInt else 0
    }
  }
}
