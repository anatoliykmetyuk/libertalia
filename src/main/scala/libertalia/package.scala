import scala.collection.JavaConversions._

import java.io.File
import org.apache.commons.io.FileUtils

package object libertalia {
  type ProcessCmd = PartialFunction[List[String], String]

  val config = {
    import Config._

    if (!configFile.exists) {
      println(s"\n\n===\nPlease configure a text file editor at ${configFile.getAbsolutePath}\n===\n")
      FileUtils.write(configFile, "editor=open\nquanta_per_hour=1", encoding)
    }

    val cfgMap: Map[String, String] = FileUtils.readLines(configFile, encoding)
      .toList
      .filter(!_.isEmpty)
      .flatMap { _.split('=').toList match {
        case k :: v :: Nil => Some(k -> v)
        case _ => None
      }}.toMap
    Config(editorPath = cfgMap("editor"), quantaPerHour = cfgMap("quanta_per_hour").toDouble)
  }
}
