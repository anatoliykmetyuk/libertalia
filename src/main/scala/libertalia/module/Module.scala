package libertalia
package module

trait Module {
  val name: String
  val description: String
  def instanceProcessor: ProcessCmd
  def processor: ProcessCmd = instanceProcessor orElse { case x => s"Unknown command: ${'"'}${x.mkString(" ")}${'"'}. For help, enter ${'"'}$name help${'"'}." }
}