package org.openintent.devkit
import org.mozilla.javascript._
import akka.actor._
import dispatch.json.Js
import java.io.File
import akka.util.duration._
import scala.reflect.BeanProperty
import com.iaasframework.engines._

import descriptors._
import modules._
import sjson.json._
import EngineDescriptorProtocol._
import JsonSerialization._
import dispatch.json.JsString
import akka.util.Timeout
import akka.dispatch.Await
import akka.pattern.ask
class EngineHost(cx: Context, scope: Scriptable, context: Map[String, String]) {
  //val engineRef = context.parent
  var internalModel: Scriptable = null;
  // Method jsConstructor defines the JavaScript constructor
  def jsConstructor() = {}
  // The class name is defined by the getClassName method
  @Override
  def getClassName(): String = { "Engine" }

  def sendMessageBody(body: Object): Object = {
    val json = RhinoUtils.toJson(body)
    val msg = EngineMessage(context, Map.empty, Js(json))
    implicit val timeout = Timeout(15 seconds)
    val future = EngineHost.instanceActor ? msg // enabled by the “ask” import
    val resp = Await.result(future, EngineHost.timeout.duration).asInstanceOf[String]
    resp
  }

  def jsFunction_update() = {

  }

  def getModel(): Scriptable = {
    internalModel
  }
  def setModel(model: Scriptable) = {
    internalModel = model
  }
}

object EngineHost {
  var context: Map[String, String] = Map.empty;
  var instanceActor: ActorRef = null;
  implicit val timeout = Timeout(15 seconds)
  val system = ActorSystem("devkit");
  def loadInstance(instanceFile: String) = {
    val file = new File(instanceFile)
    println("Loading instance from " + instanceFile)
    if (!file.exists) {
      println("File " + instanceFile + " not found")
    } else {
      val loadedStr = Js(io.Source.fromFile(instanceFile).getLines.mkString)
      val engineInstance = fromjson[EngineDescriptor](loadedStr)
      instanceActor = system.actorOf(Props(new Engine(engineInstance)), engineInstance.name);
      context = engineInstance.modules(0).settings
    }

  }

  def connect() = {
    val req = """ { "connectRequest": { "max_retries": "10" } } 
              """
    val msg = EngineMessage(context, Map.empty, Js(req))
    val future = instanceActor ? msg
    val resp = Await.result(future, timeout.duration).asInstanceOf[String]
  }

  def disconnect() = {
    val req = """ { "disconnectRequest": { "max_retries": "10" } } 
              """
    val msg = EngineMessage(context, Map.empty, Js(req))
    val future = instanceActor ? msg
    val resp = Await.result(future, timeout.duration).asInstanceOf[String]

  }

}
