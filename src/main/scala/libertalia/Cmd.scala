package libertalia

/**
 * Common commands that appear in multiple entities.
 */
object Cmd {
  val exit   = "exit"
  val help   = "help"

  val create = "mk"  // Make
  val update = "ch"  // Change
  val delete = "rm"  // Remove
  val move   = "mv"  // Move
  val open   = "op"  // Open
  val of     = "of"  // Of, possessive. Returns only entities belonging to a particular foreign entity.
  val list   = "l"   // List entities
}