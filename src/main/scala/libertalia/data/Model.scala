package libertalia.data

import org.scalarelational.mapper._

object Model {
  case class Organization(name: String, id: Option[Int] = None)
}