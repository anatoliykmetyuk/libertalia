package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

object Document extends CrudModule[Model.Document, Datastore.docs.type] with PossessiveModule[Model.Document, Datastore.docs.type] with ShowDocument {
  override val name   = "doc"
  override val source = Datastore.docs
  val editor          = new util.FileEditor(config.editorPath)

  val documentProcessor: ProcessCmd = {
    case Cmd.create :: owner :: name     :: Nil  => create(Model.Document(name, editor.create(), owner.toInt))
    case Cmd.update :: id    :: name     :: Nil  => update(id.toInt) { d => d.copy(name = name, text = editor.edit(d.text)) }
    case Cmd.open   :: id                :: Nil  => update(id.toInt) { d => d.copy(             text = editor.edit(d.text)) }
    case Cmd.move   :: id    :: newOwner :: Nil  => update(id.toInt) { d => d.copy(owner = newOwner.toInt)                  }
  }

  override val processor = documentProcessor orElse possessiveProcessor orElse crudProcessor
}

trait ShowDocument extends ShowEntity[Model.Document] {
  val snippetSize = 100

  override implicit val showEntity: Show[Model.Document] = Show.show { doc =>
    import doc._
    s"${id.get} $name; owned by: $owner; snippet:\n${text.take(snippetSize)}"    
  }

  override implicit val showList: Show[List[Model.Document]] = Show.show(_.map(_.show).mkString("\n\n"))
}
