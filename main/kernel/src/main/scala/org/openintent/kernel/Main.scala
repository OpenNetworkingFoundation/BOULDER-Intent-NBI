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

import akka.actor.ActorSystem
import akka.kernel.Bootable
import java.io.File
import java.lang.Boolean.getBoolean
import java.net.URLClassLoader
import java.util.jar.JarFile
import scala.collection.JavaConverters._

/**
 * Main class for running the microkernel.
 */
object Main {
  val quiet = getBoolean("akka.kernel.quiet")

  def log(s: String) = if (!quiet) println(s)

  def main(args: Array[String]) = {

    log(banner)
    log("Starting IaaS Framework...")
    System.setProperty("logger.resource", "logback.xml")

    val classLoader = createClassLoader()

    Thread.currentThread.setContextClassLoader(classLoader)

    val kernelBoot: Bootable = new Boot();
    //log("Starting up " + kernelBoot)
    kernelBoot.startup()

    addShutdownHook(kernelBoot)

    //  log("Successfully started IaaS Framework")
  }

  def createClassLoader(): ClassLoader = {
    if (ActorSystem.GlobalHome.isDefined) {
      val home = ActorSystem.GlobalHome.get
      val deploy = new File(home, "deploy")
      if (deploy.exists) {
        loadDeployJars(deploy)
      } else {
        log("[warning] No deploy dir found at " + deploy)
        Thread.currentThread.getContextClassLoader
      }
    } else {
      log("[warning] IaaS Framework home is not defined")
      Thread.currentThread.getContextClassLoader
    }
  }

  def loadDeployJars(deploy: File): ClassLoader = {
    val jars = deploy.listFiles.filter(_.getName.endsWith(".jar"))

    val nestedJars = jars flatMap { jar =>
      val jarFile = new JarFile(jar)
      val jarEntries = jarFile.entries.asScala.toArray.filter(_.getName.endsWith(".jar"))
      jarEntries map { entry => new File("jar:file:%s!/%s" format (jarFile.getName, entry.getName)) }
    }

    val urls = (jars ++ nestedJars) map { _.toURI.toURL }

    urls foreach { url => log("Deploying " + url) }

    new URLClassLoader(urls, Thread.currentThread.getContextClassLoader)
  }

  def addShutdownHook(bootable: Bootable): Unit = {
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      def run = {
        log("")
        log("Shutting down IaaS Framework...")

        //    log("Shutting down " + bootable.getClass.getName)
        bootable.shutdown()

        log("Successfully shut down IaaS Framework")
      }
    }))
  }

  def banner = """
================================================================================
 _____             _____  ______                                           _    
|_   _|           /  ___| |  ___|                                         | |   
  | |   __ _  __ _\ `--.  | |_ _ __ __ _ _ __ ___   _____      _____  _ __| | __
  | |  / _` |/ _` |`--. \ |  _| '__/ _` | '_ ` _ \ / _ \ \ /\ / / _ \| '__| |/ /
 _| |_| (_| | (_| /\__/ / | | | | | (_| | | | | | |  __/\ V  V / (_) | |  |   < 
 \___/ \__,_|\__,_\____/  \_| |_|  \__,_|_| |_| |_|\___| \_/\_/ \___/|_|  |_|\_\

================================================================================
"""
}
