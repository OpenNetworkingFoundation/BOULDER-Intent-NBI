package org.openintent.kernel
import akka.actor.Actor
import akka.event.Logging
import scala.collection.JavaConversions._
import akka.actor.Props

class KernelActor extends Kernel with Actor {
  val nameSpace = "com.iaasframework"
  val log = Logging(context.system, this)
  def receive = kernelReceive
  override def preStart() = {
    log.info("Starting IaaS Framework Kernel");
    val subsystems = context.system.settings.config.getStringList("iaas.subsystems")
    subsystems.foreach(subsystem => {
      val classname = Class.forName(subsystem)
      val lastdotidx = subsystem.lastIndexOf(".")
      val previousdotidx = subsystem.lastIndexOf(".", lastdotidx - 1)
      val packagename = subsystem.substring(previousdotidx + 1, lastdotidx)
      log.debug("Starting Actor for Class: " + classname)
      context.actorOf(Props(classname.newInstance().asInstanceOf[Actor]), packagename)
    })
  }
}
