package org.openintent.kernel

import com.typesafe.config._
import akka.actor.ActorSystem
import akka.kernel.Bootable
import akka.actor.Props
import akka.actor.Actor

class Boot extends Bootable {
  val config = ConfigFactory.load()
  val system = ActorSystem("iaas", config.getConfig("iaas").withFallback(config))
  system.actorOf(Props[KernelActor], "kernel")
  def startup = {
    //  play.core.server.NettyServer.main(Array[String]())
  }

  def shutdown = {
    system.shutdown()
  }

}
