/*
   Copyright 2015 Inocybe Technologies inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.openintent.engines

import akka.actor.Actor
import com.iaasframework.engines.instances._
import akka.event.Logging
import sjson.json._
import descriptors.EngineDescriptorProtocol._
import JsonSerialization._
import com.iaasframework.datastores._
import akka.util.duration._
import akka.pattern.ask
import akka.dispatch.Await
import akka.actor.Props
import dispatch.json._
import descriptors._
import akka.actor.ActorLogging

sealed trait EnginesSystemMessage
case object EnginesSystemInfo extends EnginesSystemMessage

class System extends Actor with ActorLogging {

  def receive = {
    case EnginesSystemInfo ⇒
      sender ! "Engines Subsystem: Running"
    case _ ⇒
  }
  override def preStart() {
    log.info("Starting Engines Subsystem")
    val future = context.actorFor("../datastores").ask(ViewQuery("engine_instances/all"))(5 seconds)
    val result = Await.result(future, 5 seconds).asInstanceOf[ViewResults]
    result.results foreach (engine => {
      val loadedStr = Js(engine.toString())
      val engineInstance = fromjson[EngineDescriptor](loadedStr)
      context.actorOf(Props(new Engine(engineInstance)), engineInstance.name)
      log.info("Starting engine:" + engineInstance.name)
    })
  }
}
