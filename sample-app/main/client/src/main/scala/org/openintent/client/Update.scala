/**
 *
 */
package org.openintent.client
import java.io.File
import org.clapper.argot.ArgotConverters.convertFlag
import org.clapper.argot.ArgotConverters.convertString
import net.liftweb.json._

/**
 * Update an instance. Depending on the flags, you can overwrite the entire instance
 * descriptor, or update individual parameters
 *
 *
 */
class Update extends Command {
  val instanceId = parser.parameter[String]("instance_id", "id of the instance to update", false)

  val property = parser.option[String](List("p", "parameter"), "name/value pair", "update a single property")

  val fileParam = parser.option[File](List("f", "file"), "file", "update the entire instance descriptiion") {
    (s, opt) =>

      val file = new File(s)
      if (!file.exists)
        parser.usage("Instance file \"" + s + "\" does not exist.")

      file
  }

  def process(subsystem: String) = {

    if (!fileParam.value.isDefined && !property.value.isDefined)
      parser.usage("You must set either the -i or -p option to speficy how to update the instance")

    else if (fileParam.value.isDefined && property.value.isDefined)
      parser.usage("the -i or -p options cannot be used together")

    val id = instanceId.value.get;

    println("File flag: " + fileParam.value)
    println("Property flag: " + property.value)
    println("Instance to get: " + id)

    var instanceFile: String = null

    if (fileParam.value.isDefined) {
      //Get the instance file
      val f = fileParam.value.get
      instanceFile = FileHandler.getInstanceFile(f)
    }

    //Get the document to update from the DB
    val remote = new RestClient(subsystem)
    val response = remote.get(id, "instance", true)
    var document: String = null

    response match {
      case Right(result) => document = result
      case Left(error) => println(error)
    }

    //Get the revision tag from the document
    var rev: String = null
    response match {
      case Right(result) => {
        val json = parse(result)
        rev = (json \ "_rev").values.toString()
      }
      case Left(error) => throw new Exception("\nError: " + error)
    }

    println("Updating Instance " + id + " with rev: " + rev)

    val json = parse(instanceFile)

    //write in the rev number
    val updatedDoc = json.transform {
      case JField("_rev", JString(value)) => JField("_rev", JString(rev))
      //TODO If updating based on properties, do it here.
    }

    //render it back
    import net.liftweb.json.JsonDSL._
    val updatedJson = pretty(render(updatedDoc))
    println("\n New DOC:\n " + updatedJson)

    //Put it back in the DB.
    val putResponse = remote.put(updatedJson, id)

    putResponse match {
      case Right(result) => {
        val json = parse(result)
        println("Result: \n" + pretty(render(result)))
      }
      case Left(error) => println(error)
    }
  }
}
