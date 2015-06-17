package org.openintent.devkit
import org.mozilla.javascript._
import com.iaasframework.kernel.plugins.PluginProtocol._
import com.iaasframework.engines.EngineProcessor
import com.iaasframework.engines.types.EngineTypeProtocol._

class ContextLoader(cx: Context, main: Scriptable, plugin: Plugin) {
  def getContext(contextPath: String): Option[Scriptable] = {
    val pieces = contextPath
    val matched = plugin.extensions.filter(ext => ext.name == pieces)
    val scope = cx.newObject(main)
    scope.setPrototype(main)
    val engineProcessor = new EngineProcessor();
    val engineType = engineProcessor.process("source/" + matched.head.file)
    val path = "source/" + matched.head.file.substring(0, matched.head.file.lastIndexOf("/") + 1)

    engineType match {
      case Some(x: EngineType) =>
        val sim = x.modules.filter(mod => mod.name == "simulator")
        val map = cx.newObject(scope)
        val args = sim.head.settings + ("current_folder" -> path)
        val engine = new EngineHost(cx, scope, args);

        val jsObj = Context.javaToJS(engine, scope);
        ScriptableObject.putProperty(scope, "engine", jsObj);
        val engineRef = scope.get("engine", scope).asInstanceOf[Scriptable];

        val m = classOf[EngineHost].getMethod("connect")
        val funcObj = new FunctionObject("connect", m, scope);
        scope.put("connect", scope, funcObj);

        val met = classOf[EngineHost].getMethod("disconnect")
        val funcObj2 = new FunctionObject("disconnect", met, scope);
        scope.put("disconnect", scope, funcObj2);

        val met2 = classOf[EngineHost].getMethod("loadInstance", classOf[java.lang.String])
        val funcObj3 = new FunctionObject("load", met2, scope);
        scope.put("load", scope, funcObj3);

        val model = cx.newObject(scope);
        engineRef.put("model", scope, model);

      case None =>

    }
    Some(scope)
  }
  def listContext(): List[String] = {
    plugin.extensions.map(e => e.name)
  }
}
