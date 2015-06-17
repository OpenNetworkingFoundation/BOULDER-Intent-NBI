package org.openintent.devkit

import org.mozilla.javascript._
import java.io._
import java.util.zip._
import org.apache.commons.io.IOUtils
import org.apache.commons.io.filefilter.IOFileFilter
import dispatch._
import dispatch.json._
import dispatch.json.JsHttp._
import com.iaasframework.kernel.plugins._

import PluginProtocol._
import sjson.json.JsonSerialization._
import dispatch.json._
import scouch.db.Couch
import scouch.db.Db
import scouch.db.Doc

object PublishJob extends Job {
  def getJobName = "Publish"
  def getJobDescription = "Publish the plugin for distribution"

  /**
   * Main entry point.
   *
   * Process arguments as would a normal Java program. Also
   * create a new Context and associate it with the current thread.
   * Then set up the execution environment and begin to
   * execute scripts.
   */
  def executeJob(args: Map[String, String]) = {
    println("Uploading plugin to database")
    val dir = new File("source");
    //    val settings = DataStoresSettings(context.system)
    val http = new Http with thread.Safety with NoLogging
    val couch = Couch("localhost") //if (settings.username == "") Couch(settings.host) else Couch(settings.host, settings.username, settings.password)
    val db = Db(couch, "default")

    if (dir.isDirectory) {
      println("Loading files from source folder")
      def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)
      val manifest = new File("source/Manifest.json")
      val manifestJson = Js(io.Source.fromFile(manifest).getLines().mkString)
      val plugin = fromjson[Plugin](manifestJson)
      val manifestDoc = Doc(db, plugin.info.name)
      try {
        val ir = http(db getRef plugin.info.name)
        http(manifestDoc update (plugin, ir._2))
      } catch {
        case e: dispatch.StatusCode =>
          http(manifestDoc add plugin)
      }
      filesAt(dir).foreach(file => {
        if (file.getName != "Manifest.json") {
          val source = scala.io.Source.fromFile(file)
          val byteArray = source.map(_.toByte).toArray
          source.close()
          val ir = http(db getRef plugin.info.name)
          val doc = Doc(db, ir._1)
          http(doc attach (dir.toURI().relativize(file.toURI()).getPath(), "text/javascript", byteArray, Some(ir._2)))
          println(dir.toURI().relativize(file.toURI()).getPath())
        }
      });

    } else println("Missing Source Folder");

  }
}

