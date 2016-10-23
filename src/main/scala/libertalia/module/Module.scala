package libertalia
package module

trait Module {
  val name: String
  val description: String
  def instanceProcessor: ProcessCmd
  def helpData: List[Module.HelpEntry] = Nil

  def processor: ProcessCmd = instanceProcessor orElse { case x => s"Unknown command: ${'"'}${x.mkString(" ")}${'"'}. For help, enter ${'"'}$name help${'"'}." }
}

object Module {
  type HelpEntry = (List[(String, Boolean)], String)

  val all = List(Organization, Document, Message, Time, Help)
}
