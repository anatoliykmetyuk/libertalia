package libertalia
package module

object Organization extends Module {
  override val name = "org"
  override val processor: ProcessCmd = {
    case Cmd.create :: name :: Nil       => s"Create new organization $name"
    case Nil                             => s"List all organizations"
    case Cmd.update :: id :: name :: Nil => s"Update organization $id with $name"
    case Cmd.delete :: id :: Nil         => s"Delete organization $id"
  }
}