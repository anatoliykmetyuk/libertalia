package libertalia

import scala.collection.JavaConversions._
import scala.util._

import org.apache.commons.io.IOUtils

import libertalia.module._
import libertalia.data._

import scala.tools.jline.console.ConsoleReader

object Main {
  val encoding = "utf8"
  val modules  = Seq(Organization, Document, Message, Time)

  val defaultProcessor: ProcessCmd = { case moduleName :: args =>
    modules.find(_.name == moduleName).map(_.processor(args))
      .getOrElse(s"Unknown module: $moduleName")
  }

  def main(args: Array[String]): Unit = {
    Datastore.createOrUpgrade()

    val console = new ConsoleReader()
    console.setPrompt("libertalia> ")

    var stop = false
    while (!stop) {
      val line = console.readLine
      if (Cmd.exit == line) stop = true
      else if (!line.isEmpty) Try { defaultProcessor(parseArgs(line)) } match {
        case Success(res) => console.println(res)
        case Failure(ex ) => ex.printStackTrace //print(ex.getMessage + prompt)
      }
    }
  }

  def parseArgs(line: String): List[String] = {
    val chars = """a-zA-Z0-9\!\@\#\$\%\^\&\*\(\)\_\+\-';,\."""
    val cmds  = s"""[$chars]+|\"[$chars ]+\"""".r

    cmds.findAllIn(line).toList.map(_.filter(_ != '"'))
  }
}
