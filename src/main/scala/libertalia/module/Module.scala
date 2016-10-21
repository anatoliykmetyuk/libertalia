package libertalia
package module

trait Module {
  val name: String
  val processor: ProcessCmd
}