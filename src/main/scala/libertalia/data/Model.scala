package libertalia.data

import org.scalarelational.mapper._

object Model {
  case class Organization(
    name: String
  , parent: Option[Int]
  , id: Option[Int] = None
  )

  case class Document(
    name : String
  , text : String
  , owner: Int
  , id   : Option[Int] = None
  )

  case class Message(
    title    : String
  , text     : String
  , sender   : Int
  , recipient: Int
  , timestamp: java.sql.Timestamp = new java.sql.Timestamp(System.currentTimeMillis)
  , id       : Option[Int] = None
  , seen     : Boolean = false
  )

  case class Time(
    amount   : Int
  , reason   : String
  , owner    : Int
  , timestamp: java.sql.Timestamp = new java.sql.Timestamp(System.currentTimeMillis)
  , id       : Option[Int] = None
  )
}