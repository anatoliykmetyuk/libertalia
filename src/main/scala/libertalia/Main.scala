package libertalia

import scala.collection.JavaConversions._
import scala.util._

import org.apache.commons.io.IOUtils

import libertalia.module._
import libertalia.data._

object Main {
  val encoding = "utf8"
  val prompt   = "\nlibertalia> "
  val modules  = Seq(Organization, Document, Message)

  val defaultProcessor: ProcessCmd = { case moduleName :: args =>
    modules.find(_.name == moduleName).map(_.processor(args))
      .getOrElse(s"Unknown module: $moduleName")
  }

  def main(args: Array[String]): Unit = {
    Datastore.createOrUpgrade()
    print("Libertalia v0.0.1 console" + prompt)

    lazy val it: Iterator[String] = IOUtils.lineIterator(System.in, encoding)

    var stop = false
    while(!stop && it.hasNext) {
      val line = it.next
      if (Cmd.exit == line) stop = true
      else if (!line.isEmpty) Try { defaultProcessor(parseArgs(line)) } match {
        case Success(res) => print(res + prompt)
        case Failure(ex ) => ex.printStackTrace //print(ex.getMessage + prompt)
      }
      else print(prompt)
    }
  }

  def parseArgs(line: String): List[String] = {
    val chars = """a-zA-Z0-9\!\@\#\$\%\^\&\*\(\)\_\+\-';,\."""
    val cmds  = s"""[$chars]+|\"[$chars ]+\"""".r

    cmds.findAllIn(line).toList.map(_.filter(_ != '"'))
  }
}
