package libertalia
package module

trait Module {
  val name: String
  val description: String
  val processor: ProcessCmd
}