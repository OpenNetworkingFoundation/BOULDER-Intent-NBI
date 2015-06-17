package org.openintent.devkit

import org.mozilla.javascript.Context
import java.io._
import akka.actor._
import akka.util._
import com.iaasframework.modules.simulator._
import com.iaasframework.modules.remoteshell._
import com.iaasframework.modules.execution._
import com.iaasframework.kernel.plugins.PluginProcessor
import com.iaasframework.kernel._
import com.iaasframework.engines._
import com.typesafe.config.ConfigFactory
import jline.console.ConsoleReader

object RunJob extends Job {
  def getJobName = "Run"
  def getJobDescription = "Run the plugin in a interactive environment"

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
    val config = ConfigFactory.parseString("""
      iaas {
        subsystems = ["com.iaasframework.engines.EnginesSystem"]
      }
      """)
    val cx = Context.enter();
    // startSystems
    val system = ActorSystem("iaas", ConfigFactory.load(config))
    //    system.actorOf(Props[KernelActor], "kernel")

    System.setProperty("jline.shutdownhook", "true");

    try {
      // Initialize the standard objects (Object, Function, etc.)
      // This must be done before scripts can be executed.
      val scope = cx.initStandardObjects();
      cx.getWrapFactory().setJavaPrimitiveWrap(false);
      PluginProcessor.process(args) match {
        case Some(plugin) =>
          val shell = InteractiveShell.create(cx, plugin);
          System.err.print("IaaS Framework Shell.\nType: help() for usage information\n");
          shell.start()
        case None =>
          println("ERROR: Could not start shell!")
      }
    } finally {
      Context.exit();
      system.shutdown();
    }
  }
}

