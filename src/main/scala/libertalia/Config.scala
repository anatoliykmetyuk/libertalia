package libertalia

import java.io.File

case class Config(editorPath: String, quantaPerHour: Double)

object Config {
  val encoding = "utf8"
  val userHome = new File(System.getProperty("user.home"))
  val appHome  = new File(userHome, ".libertalia")
  def file(name: String) = new File(appHome, name)

  val configFile     = file(".config" )
  val commandHistory = file(".history")
}