package com.iaasframework.client

import java.io.File
import org.clapper.argot.ArgotConverters._
import org.clapper.argot._
import net.liftweb.json._

/**
 * Create a new instance of an Intent based application. 
 * The instance file must be supplied.
 *
 *
 */
class Create extends Command {

  val fileParam = parser.parameter[File]("path", "path to instance file", false) {
    (s, opt) =>

      val file = new File(s)
      if (!file.exists)
        parser.usage("Instance file \"" + s + "\" does not exist.")

      file
  }

  def process(subsystem: String) {
    //Get the file 
    val f = fileParam.value.get
    val instanceFile = FileHandler.getInstanceFile(f)

    val json = parse(instanceFile)
    val id = (json \ "name").values.toString()

    println("ID: " + id)

    val remote = new RestClient(subsystem)
    val response = remote.put(instanceFile, id)
    //remote.post(instanceFile)

    response match {
      case Right(result) => {
        println("Result: \n" + pretty(render(parse(result))))
      }
      case Left(error) => println(error)
    }
  }
}
