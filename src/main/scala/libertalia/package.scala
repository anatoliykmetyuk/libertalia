package object libertalia {
  type ProcessCmd = PartialFunction[List[String], String]
  val config = Config("/Applications/MacDown.app/Contents/MacOS/MacDown")
}