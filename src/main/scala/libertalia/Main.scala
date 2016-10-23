package libertalia

import scala.collection.JavaConversions._
import scala.util._

import org.apache.commons.io.IOUtils

import libertalia.module._
import libertalia.data._

import scala.tools.jline.console.ConsoleReader
import scala.tools.jline.console.history.FileHistory
import java.io.File

object Main {
  val version = "0.0.1"

  val defaultProcessor: ProcessCmd = { case moduleName :: args =>
    Module.all.find(_.name == moduleName).map(_.processor(args))
      .getOrElse(s"Unknown module: $moduleName")
  }

  val helpMessage = {
    val modulesStr = Module.all.map { m => String.format("%-5s - %s", m.name, m.description) }.mkString("\n")
    s"""
   |Format of commands: <module_name> <module_command>
   |Valid modules:
   |$modulesStr
   |
   |To find out more about what <module_command> each module accepts, enter: help <module_name>.
   |""".stripMargin
 }

  def main(args: Array[String]): Unit = {
    Datastore.createOrUpgrade()
    println(s"\n\n\nLibertalia v$version")
    println("Enter \"help\" to get started (without quotes)")

    val console = new ConsoleReader()
    console.setPrompt("libertalia> ")
    console.setHistory(new FileHistory(Config.commandHistory)) 

    var stop = false
    try while (!stop) {
      val line = console.readLine

      line match {
        case Cmd.exit => stop = true
        case Cmd.help => console.println(helpMessage)
        case _ if line.nonEmpty => Try { defaultProcessor(parseArgs(line)) } match {
          case Success(res) => console.println(res)
          case Failure(ex ) => ex.printStackTrace //print(ex.getMessage + prompt)
        }

        case _ =>
      }
    } finally console.getHistory.asInstanceOf[FileHistory].flush()
  }

  def parseArgs(line: String): List[String] = {
    val chars = """a-zA-Z0-9\!\@\#\$\%\^\&\*\(\)\_\+\-';,\."""
    val cmds  = s"""[$chars]+|\"[$chars ]+\"""".r

    cmds.findAllIn(line).toList.map(_.filter(_ != '"'))
  }
}
