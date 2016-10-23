package libertalia
package module

import data._

import cats.Show
import cats.syntax.show._

object Message extends CrudModule[Model.Message, Datastore.msgs.type] with PossessiveModule[Model.Message, Datastore.msgs.type]  with ShowMessage {
  override val name        = "msg"
  override val description = "Messages between organizations"
  override val source      = Datastore.msgs
  val editor               = new util.FileEditor(config.editorPath)

  val instanceProcessor: ProcessCmd = {
    case Cmd.create :: from  :: to :: title :: Nil  => create            { Model.Message(title, editor.create(), from.toInt, to.toInt)  }
    case Cmd.update :: id          :: title :: Nil  => update (id.toInt) { d => d.copy(title = title)                                   }
    case Cmd.update :: id                   :: Nil  => update (id.toInt) { d => d.copy(text  = editor.edit(d.text))                     }
    case "tgl"      :: id                   :: Nil  => update (id.toInt) { d => d.copy(seen  = !d.seen)                                 }
    case Cmd.open   :: id                   :: Nil  => inspect(id.toInt) { d => editor.read(d.text)                                     }
  }
}

trait ShowMessage extends ShowEntity[Model.Message] {
  val snippetSize = 100

  override implicit val showEntity: Show[Model.Message] = Show.show { msg =>
    import msg._
    s"${id.get} Seen: $seen; Time: ${timestamp.show}; From: $sender; Title: $title"
  }
}
