package org.openintent.devkit

import org.mozilla.javascript._
import java.io._
import com.iaasframework.kernel.plugins.PluginProtocol._
import com.iaasframework.engines.types.EngineTypeProtocol._

import sjson.json.JsonSerialization._
import dispatch.json._

object CompileJob extends Job {
  def getJobName = "Compile"
  def getJobDescription = "Check the plugin for compilation errors"

  /**
   * Main entry point.
   *
   * Process arguments as would a normal Java program. Also
   * create a new Context and associate it with the current thread.
   * Then set up the execution environment and begin to
   * execute scripts.
   */
  def executeJob(args: Map[String, String]) = {
    // Associate a new Context with this thread
    val cx = Context.enter();
    try {
      // Initialize the standard objects (Object, Function, etc.)
      // This must be done before scripts can be executed.
      val scope = cx.initStandardObjects();

    } finally {
      Context.exit();
    }
  }

  def processEngine(cx: Context, scope: Scriptable, folder: String) = {
    val engine = new File(folder + "/engine.json")
    if (engine.exists()) {
      val engineTypeJs = io.Source.fromFile(folder + "/engine.json").getLines.mkString
      val engineType = fromjson[EngineType](Js(engineTypeJs))
      engineType.modules.foreach(
        module => {
          println("<--" + module.name + "-->")
          //         processModule(cx, scope, module.folder)
        })
    } else
      println("Couldn't find the engine.json file in " + folder)
  }

  def processModule(cx: Context, scope: Scriptable, folder: String) = {
    val engine = new File(folder + "/module.json")
    if (engine.exists()) {
      val engineTypeJs = io.Source.fromFile(folder + "/module.json").getLines.mkString
      val engineType = fromjson[EngineType](Js(engineTypeJs))
      engineType.modules.foreach(
        module =>
          println("<--" + module.name + "-->"))
    } else
      println("Couldn't find the module.json file in " + folder)

  }
  def processSource(cx: Context, scope: Scriptable, filename: String) =
    {
      val in = new FileReader(filename);
      try {
        cx.evaluateReader(scope, in, filename, 1, null);
      } catch {
        case we: WrappedException =>
          System.err.println(we.getWrappedException().toString());
          we.printStackTrace();
        case ee: EvaluatorException =>
          System.err.println("js: " + ee.getMessage());
        case jse: JavaScriptException =>
          System.err.println("js: " + jse.getMessage());
        case ioe: IOException =>
          System.err.println(ioe.toString());
        case ex: FileNotFoundException =>
          Context.reportError("Couldn't open file \"" + filename + "\".");

      } finally {
        in.close();
      }

    }
}

