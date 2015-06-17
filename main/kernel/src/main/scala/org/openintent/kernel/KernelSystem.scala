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
package org.openintent.kernel

import akka.actor.Actor
import akka.event.Logging
import akka.actor.ActorLogging

/**
 * Kernel Service messages for the Actors
 */
sealed trait KernelSystemMessage extends Serializable
object InitializeSystem extends KernelSystemMessage
object GetStatus extends KernelSystemMessage
case class SystemStatus(name: String, status: String)
case class ShutdownSystem() extends KernelSystemMessage

/**
 * Trait used to create Kernel Services Actors
 */
trait KernelSystem extends Actor with ActorLogging { this: Actor ⇒
  var name: String = "Default"
  var status: String = "Stopped"

  // to be defined in subclassing actor
  def systemMessageHandler: Receive

  def receive = systemMessageHandler orElse serviceManagement

  protected def initialize() = {}

  def getStatus: String = {
    status
  }

  def serviceManagement: Receive = {
    case InitializeSystem ⇒
    //initialize(config)
    case GetStatus =>
      sender ! SystemStatus(name, status)
    case _ ⇒
      log.info("Kernel Service Received Unknown Message")
  }

}
