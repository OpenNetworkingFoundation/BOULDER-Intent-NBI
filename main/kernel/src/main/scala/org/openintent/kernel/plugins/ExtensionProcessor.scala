package org.openintent.kernel.plugins
import com.iaasframework.kernel.plugins.PluginProtocol._
import java.io.File

trait ExtensionProcessor {
  protected def handleExtension(fileData: String): Option[Any]

  def process(file: String, options: Map[String, String] = Map.empty): Option[Any] = {
    val extFile = new File(file)
    if (extFile.exists()) {
      val data = io.Source.fromFile(extFile).getLines.mkString
      handleExtension(data)
    } else {
      println("File " + file + " not found.")
      None
    }
  }

}
