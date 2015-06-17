package org.openintent.kernel.plugins
import java.io.File
import org.mozilla.javascript._
import java.io._
import PluginProtocol._
import sjson.json.JsonSerialization._
import dispatch.json._

object PluginProcessor {

  def process(options: Map[String, String]): Option[Plugin] = {
    val sourceFolder = options.getOrElse("source_directory", "source")
    val manifest = new File(sourceFolder + "/Manifest.json")
    val quiet = options.getOrElse("quiet", false).asInstanceOf[Boolean]

    if (manifest.exists()) {
      val jsonSrc = io.Source.fromFile(manifest).getLines.mkString
      val pluginJs = Js(jsonSrc)
      val plugin = fromjson[Plugin](pluginJs)
      if (!quiet) println("""
==========================
   Plugin Information
==========================
""")
      println("Plugin Name: " + plugin.info.name)
      println("Plugin Version: " + plugin.info.version)
      println("Plugin Summary: " + plugin.info.summary)
      plugin.extensions.foreach(ext ⇒ { println("Found " + ext.name + " extension to " + ext.system + " system") })
      return Some(plugin)
    } else
      println("Could not find Manifest.json file in :" + sourceFolder)
    None
  }

  def processExtension(extension: Extension) = {
  }

  def processAll() = {}
}
