package org.openintent.kernel.plugins
import akka.actor._

class PluginManagerActor extends Actor {
  val nameSpace = "com.iaasframework"

  def receive = {
    case _ =>
      println("Receive")
  }

}
