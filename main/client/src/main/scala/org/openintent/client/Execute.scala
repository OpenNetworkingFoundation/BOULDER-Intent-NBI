/**
 *
 */
package org.openintent.client

import org.clapper.argot.ArgotConverters._
import org.clapper.argot._

import net.liftweb.json._

/**
 * Execute an intent on instance.
 * usage: execute <subject> <intent> <object> [parameters]
 *
 */
class Execute extends Command {

  val command = parser.parameter[String]("command", "the name of command to execute", false)
  val instance = parser.parameter[String]("instance_id", "id of the instance to act upon", false)
  val options = parser.multiParameter[String]("parameters", "a space separated list of name=value parameters for the command if any.", true) {
    (s, param) =>
      validParam.findFirstIn(s) match {
        case None => parser.usage("\n Parameter \"" + s + "\" must be in the form of key=value")
        case Some(_) => s
      }
  }
  val validParam = """(\S+)=(\S+)""".r

  def process(subsystem: String): Unit = {

    var json = "{ \"behavior\": \"" + command.value.get + "\", \"parameters\":{"

    try {
      if (options.value != Nil) {
        for (x <- 0 until options.value.size - 1) {
          val validParam(name, value) = options.value(x)
          json += "\"" + name + "\": \"" + value + "\","
        }
        //add the last pair without the comma
        val validParam(name, value) = options.value(options.value.size - 1)
        json += "\"" + name + "\": \"" + value + "\""

      }
      json += "}}"
    } catch {
      case ex: Exception => ex.printStackTrace()
    }

    println("Executing command: " + command.value.get + " on instance: " + instance.value.get)
    println("  with parameters: " + options.value)

    println("JSON: " + json)

    val remote = new RestClient(subsystem)
    val response = remote.execute(instance.value.get, json)
    //remote.post(instanceFile)

    response match {
      case Right(result) => {
        println("Result: \n" + pretty(render(parse(result))))
      }
      case Left(error) => println(error)
    }

  }
}
