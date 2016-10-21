package libertalia
package module

import data._

import cats._
import cats.syntax.all._
import cats.instances.all._

object Document extends Module with ShowList {
  override val name = "doc"
  override val processor: ProcessCmd = {
    case Cmd.create :: owner :: name :: Nil  => create(owner.toInt, name)
    case Nil                                 => list()
    case id :: Nil                           => read(id.toInt)
    case Cmd.update :: id :: name :: Nil     => update(id.toInt, name)
    case Cmd.delete :: ids                   => delete(ids.map(_.toInt))
    case Cmd.move   :: id :: newOwner :: Nil => move(id.toInt, newOwner.toInt)
    case Cmd.open   :: id :: Nil             => open(id.toInt)
    
    case x => s"Unknown command: $x"
  }

  val docs = Datastore.docs

  val editor = new util.FileEditor(config.editorPath)

  def create(owner: Int, name: String): String = {
    val text  = editor.create()
    val id = docs.create(Model.Document(name, text, owner))
    report(id, "Document created")
  }

  def list(): String = {
    val allDocs: List[Model.Document] = docs.all
    showList(allDocs)
  }

  def read(id: Int): String = docs.get(id).toString

  def update(id: Int, name: String) = {
    val existing = docs.get(id).get
    val newText  = editor.edit(existing.text)

    docs.modify(existing.copy(name = name, text = newText))
    report(id, "Document updated")
  }

  def open(id: Int) = {
    val doc = docs.get(id).get
    val newText = editor.edit(doc.text)

    docs.modify(doc.copy(text = newText))
    report(id, "Document updated")
  }

  def move(id: Int, newOwner: Int): String = {
    val existing = docs.get(id).get
    docs.modify(existing.copy(owner = newOwner))
    report(id, "Document updated")
  }

  def delete(ids: List[Int]) = {
    docs.remove(ids)
    s"Documents deleted: $ids"
  }

  def report(id: Int, msg: String): String = {
    val doc: Model.Document = docs.get(id).get
    s"$msg: ${showDoc(doc)}"
  }
}

trait ShowList {
  def showDoc(doc: Model.Document, snippetSize: Int = 100): String = {
    import doc._
    s"${id.get} $name; owned by: $owner; snippet:\n${text.take(snippetSize)}"
  }

  def showList(docs: List[Model.Document]): String =
    docs.map(showDoc(_)).mkString("\n\n")
}
