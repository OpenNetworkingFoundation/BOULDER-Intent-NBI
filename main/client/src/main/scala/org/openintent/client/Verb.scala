package org.openintent.client

import org.clapper.argot.ArgotConverters.convertString
import org.clapper.argot.ArgotParser
import org.clapper.argot.ArgotUsageException

/**
 * Base command class that other commands must extend. It defines
 * the subsystem parameter that is required. command must implement
 * the process method.
 *
 */
abstract class Verb {

  val parser = new ArgotParser("command")

  val ssystem = parser.parameter[String]("subsystem", "subsystem to act upon [engines|resources]", false) {
    (s, param) =>
      s match {
        case "engines" => s
        case "resources" => s
        case _ => parser.usage("\n Invalid subsystem: " + s)
      }
  }

  //point of entry for command
  def execute(args: Array[String]) = {
    try {
      if (args.length == 0)
        parser.usage();

      parser.parse(args)
      process(ssystem.value.get)

    } catch {
      case e: ArgotUsageException => println(e.message)
      case e: Exception => println(e.getMessage())
    }
  }

  //Abstract method to process the command
  def process(subsystem: String)
}

