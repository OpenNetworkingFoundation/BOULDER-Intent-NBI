package org.openintent.client

import org.clapper.argot.ArgotConverters._
import org.clapper.argot._
import net.liftweb.json._

/**
 * Delete an instance. The instance to be deleted must be given
 *
 */
class Delete extends Command {

  val instance = parser.parameter[String]("instance_id", "id of the instance to delete", false)

  def process(subsystem: String) = {
    val id = instance.value.get
    println("Instance to delete: " + id)

    val remote = new RestClient(subsystem)
    val response = remote.delete(id)
    //remote.post(instanceFile)

    response match {
      case Right(result) => {
        println("Result: \n" + pretty(render(parse(result))))
      }
      case Left(error) => println(error)
    }
  }
}
