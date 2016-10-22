package libertalia.util

import java.io.File
import org.apache.commons.io.FileUtils

/** Allows to edit strings in external applications. */
class FileEditor(editorPath: String, tmpPrefix: String = "libertalia_", tmpSuffix: String = System.currentTimeMillis.toString, encoding: String = "utf8") {
  def edit(str: String, readOnly: Boolean = false): String = {
    // Write string to the temporary file
    val tmpFile: File = File.createTempFile(tmpPrefix, tmpSuffix)
    tmpFile.deleteOnExit()
    FileUtils.write(tmpFile, str, encoding)

    // Open that file in the editora
    def exec(cmds: String*): Process = Runtime.getRuntime.exec(cmds.toArray)
    
    if (!readOnly) {
      exec(editorPath, tmpFile.getAbsolutePath).waitFor()
      FileUtils.readFileToString(tmpFile)
    }
    else {
      exec(editorPath, tmpFile.getAbsolutePath, "&")
      ""
    }
  }

  def create() = edit("")

  def read(str: String) = edit(str, true)
}