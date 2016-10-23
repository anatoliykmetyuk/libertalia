package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

object Document extends CrudModule[Model.Document, Datastore.docs.type] with PossessiveModule[Model.Document, Datastore.docs.type] with ShowDocument {
  override val name        = "doc"
  override val description = "Documents of organizations"
  override val source      = Datastore.docs
  val editor               = new util.FileEditor(config.editorPath)

  val instanceProcessor: ProcessCmd = {
    case Cmd.create :: owner :: name     :: Nil  => create(Model.Document(name, editor.create(), owner.toInt))
   
    case Cmd.update :: id    :: name     :: Nil  => update (id.toInt) { d => d.copy(name = name)                }
    case Cmd.update :: id                :: Nil  => update (id.toInt) { d => d.copy(text = editor.edit(d.text)) }
    
    case Cmd.open   :: id                :: Nil  => inspect(id.toInt) { d => editor.read(d.text)                             }
    case Cmd.move   :: id    :: newOwner :: Nil  => update (id.toInt) { d => d.copy(owner = newOwner.toInt)                  }
  }

  override def instanceHelp = List(
    List(Cmd.create -> false, "owner" -> true, "name" -> true) -> "Create a new document owned by an organization with the <owner> id"
  , List(Cmd.update -> false, "id" -> true, "name" -> true) -> "Rename the document"
  , List(Cmd.update -> false, "id" -> true) -> "Open the document for editing"
  , List(Cmd.open   -> false, "id" -> true) -> "Read-only access to the document"
  , List(Cmd.move   -> false, "id" -> true, "new_owner" -> true) -> "Transfer the document to an organization with the given id"
  )
}

trait ShowDocument extends ShowEntity[Model.Document] {
  val snippetSize = 100

  override implicit val showEntity: Show[Model.Document] = Show.show { doc =>
    import doc._
    s"${id.get} Owner: $owner; Title: $name"    
  }
}
