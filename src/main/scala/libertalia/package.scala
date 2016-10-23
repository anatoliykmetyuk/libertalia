import scala.collection.JavaConversions._

import java.io.File
import org.apache.commons.io.FileUtils

package object libertalia {
  type ProcessCmd = PartialFunction[List[String], String]

  val config = {
    val homeDir    = new File(System.getProperty("user.home"))
    val configFile = new java.io.File(homeDir, ".libertalia")
    val encoding = "utf8"

    if (!configFile.exists) {
      println(s"Please configure a text file editor at ${configFile.getAbsolutePath}")
      FileUtils.write(configFile, "editor=open", encoding)
    }

    val cfgMap: Map[String, String] = FileUtils.readLines(configFile, encoding)
      .toList
      .filter(!_.isEmpty)
      .flatMap { _.split('=').toList match {
        case k :: v :: Nil => Some(k -> v)
        case _ => None
      }}.toMap
    Config(editorPath = cfgMap("editor"))
  }
}
