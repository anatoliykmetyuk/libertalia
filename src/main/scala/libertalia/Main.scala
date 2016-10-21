package libertalia

import scala.collection.JavaConversions._

import org.apache.commons.io.IOUtils

object Main {
  val encoding = "utf8"

  type ProcessCmd = String => String

  val defaultProcessor: ProcessCmd = {
    case "hello" => "world"
  }

  def main(args: Array[String]): Unit = {
    print("Libertalia v0.0.1 console\n> ")
    lazy val it: Iterator[String] = IOUtils.lineIterator(System.in, encoding)

    var stop = false
    while(!stop && it.hasNext) {
      val line = it.next
      if (Cmd.exit == line) stop = true
      else print(defaultProcessor(line) + "\n> ")
    }
  }
}
