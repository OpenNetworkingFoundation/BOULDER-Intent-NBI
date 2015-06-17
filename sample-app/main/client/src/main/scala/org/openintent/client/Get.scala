/**
 *
 */
package org.openintent.client

import org.clapper.argot.ArgotConverters.convertFlag
import org.clapper.argot.ArgotConverters.convertString
import net.liftweb.json._

/**
 * Get the state of an instance. This could be the instance file itself, or the statefull
 * model of the instance depending on the flag given
 *
 *
 */
class Get extends Command {

  val instanceId = parser.parameter[String]("instance_id", "id of the instance to delete", false)

  //define the flags
  val instance = parser.flag[Boolean](List("i", "instance"), "Get the instance definition of this instance")
  val state = parser.flag[Boolean](List("s", "state"), "Get the instance state of this instance")

  def process(subsystem: String) = {
    //check the flags
    val iFlag = instance.value.getOrElse(false)
    val sFlag = state.value.getOrElse(false)

    if (!iFlag && !sFlag) {
      parser.usage("You must set either the -i instance flag or -s state flag")
    }

    val id = instanceId.value.get;

    println("Instance flag: " + iFlag)
    println("State flag: " + sFlag)
    println("Instance to get: " + id)

    val remote = new RestClient(subsystem)
    var response: Either[String, String] = null;

    iFlag match {
      case true => id match {
        case "ALL" => response = remote.getAll("instance")
        case _ => response = remote.get(id, "instance")
      }
      case _ => id match {
        case "ALL" => response = remote.getAll("state")
        case _ => response = remote.get(id, "state")
      }
    }

    response match {
      case Right(result) => {
        println("Result: \n" + pretty(render(parse(result))))
      }
      case Left(error) => println(error)
    }
  }

}
