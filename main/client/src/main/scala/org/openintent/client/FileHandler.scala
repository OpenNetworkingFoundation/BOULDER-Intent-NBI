package org.openintent.client

import akka.actor.ActorSystem
import akka.actor.Props
import java.io.File

/**
 * Read a JSON file and return it.
 */
object FileHandler {

  def getInstanceFile(file: File): String = {
    //read the doc from file
    println("Trying to get file from " + file.getAbsolutePath())
    val source = scala.io.Source.fromFile(file.getAbsolutePath())
    val body = source.mkString
    source.close()

    println("Instance File: \n" + body);

    body
  }
}
