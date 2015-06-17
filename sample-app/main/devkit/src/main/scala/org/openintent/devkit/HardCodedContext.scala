package org.openintent.devkit
import com.iaasframework.kernel.plugins.PluginProtocol._
import com.iaasframework.engines.instances._
import org.mozilla.javascript._
/**
 * Temporary but used to load the proper context for engine for now...
 */
object HardCodedContext {
  def contextualize(plugin: Plugin, cx: Context, scope: Scriptable) = {
    // ScriptableObject.defineClass(scope, classOf[EngineHost])
    // val engine = cx.newObject(scope, "Engine");
    // scope.put("engine", scope, engine);
  }
}
