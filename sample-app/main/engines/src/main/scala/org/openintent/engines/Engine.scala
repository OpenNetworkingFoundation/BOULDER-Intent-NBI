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

import akka.actor.{ Actor, ActorRef, FSM }
import dispatch.json._
import akka.event.Logging
import akka.actor.Props
import akka.util.duration._
import descriptors._

class EngineException extends RuntimeException

/** State LifeCycle **/
sealed trait State
case object Disconnected extends State
case object Connecting extends State
case object Uninitialized extends State
case object Error extends State
case object Active extends State
case object Processing extends State

/**
 * The Engine Actor is a finite state machine that manages a lifecycle given
 * a specific engine instance context.
 */
sealed trait EngineEventCode
case object BusyEvent extends EngineEventCode
case object IdleEvent extends EngineEventCode
case object ErrorEvent extends EngineEventCode

/** Engine Data */
sealed trait Data
case object Context extends Data

case object ConnectionRequest
case object ConnectionSuccess
case object ConnectionFailure
case object InitializeModel
case class EngineMessage(context: Map[String, String] = Map.empty, headers: Map[String, String] = Map.empty, body: JsValue)
case class EngineEvent(engineId: String, code: EngineEventCode, data: Map[String, JsValue])

class Engine(desc: EngineDescriptor) extends Actor with FSM[State, Data] {

  val modules = new scala.collection.mutable.HashMap[String, ActorRef]

  startWith(Disconnected, Context)

  when(Disconnected) {
    case Event(EngineMessage | FSM.StateTimeout, _) ⇒ stay
    case Event(ConnectionRequest, _) => goto(Connecting)
  }

  when(Connecting) {
    case Event(ConnectionFailure | FSM.StateTimeout, _) => goto(Active)
    case Event(ConnectionSuccess, _) => goto(Uninitialized)
  }

  when(Uninitialized) {
    case Event(ConnectionFailure, _) => goto(Disconnected)
  }

  when(Active) {
    case Event(ConnectionFailure, _) => goto(Disconnected)
    case Event(EngineEvent | FSM.StateTimeout, _) => goto(Active)
  }

  when(Processing) {
    case Event(ConnectionFailure, _) => goto(Disconnected)
    case Event(EngineEvent | FSM.StateTimeout, _) => goto(Active)
  }

  when(Error) {
    case Event(ConnectionFailure, _) => goto(Disconnected)
    case Event(EngineEvent | FSM.StateTimeout, _) => goto(Active)
  }

  /*  def receive = {
    case msg: EngineMessage ⇒
      log.info("Engine Message Received")
      modules.foreach(mod ⇒ { mod._2.forward(msg) })
    case RegisterModule(mod, actor) ⇒
      modules(mod) = actor
    case UnregisterModule(mod) ⇒
      modules.remove(mod)
    case _ ⇒
      sender ! "Unknown Message"
  }
*/
  /*  override def preStart() {
    log.info("Starting engine instance for " + desc.name)
    desc.modules foreach { module =>
      module.moduleType match {
        case "simulator" =>
          val classname = Class.forName("com.iaasframework.modules.simulator.SimulatorModule")
          val mod = context.actorOf(Props(classname.newInstance().asInstanceOf[Actor]), module.name);
          modules.put(module.name, mod);
        case "remoteshell" =>
          val classname = Class.forName("com.iaasframework.modules.remoteshell.RemoteShellModule")
          val mod = context.actorOf(Props(classname.newInstance().asInstanceOf[Actor]), module.name);
          modules.put(module.name, mod);
      }
    }
  }*/
}
