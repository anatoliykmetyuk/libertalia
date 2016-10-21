package libertalia.util

import java.io.File
import org.apache.commons.io.FileUtils

/** Allows to edit strings in external applications. */
class FileEditor(editorPath: String, tmpPrefix: String = "libertalia_", tmpSuffix: String = System.currentTimeMillis.toString, encoding: String = "utf8") {
  def edit(str: String): String = {
    // Write string to the temporary file
    val tmpFile: File = File.createTempFile(tmpPrefix, tmpSuffix)
    tmpFile.deleteOnExit()
    FileUtils.write(tmpFile, str, encoding)

    // Open that file in the editor
    val proc: Process = Runtime.getRuntime.exec(Array(editorPath, tmpFile.getAbsolutePath))
    proc.waitFor()

    // Read the changes to the file and return them
    FileUtils.readFileToString(tmpFile)
  }

  def create() = edit("")
}