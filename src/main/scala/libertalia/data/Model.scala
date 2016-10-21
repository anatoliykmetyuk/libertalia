package libertalia.data

import org.scalarelational.mapper._

object Model {
  case class Organization(name: String, parent: Option[Int], id: Option[Int] = None)
}