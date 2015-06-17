package com.iaasframework.client

import java.io.File
import akka.actor.ActorSystem
import akka.actor.Props
import cc.spray.can.client.HttpClient
import cc.spray.client.HttpConduit
import cc.spray.http.HttpContent.HttpContentExtractor
import cc.spray.http.ContentType
import cc.spray.http.HttpCharsets
import cc.spray.http.HttpContent
import cc.spray.http.HttpMethods
import cc.spray.http.HttpRequest
import cc.spray.http.HttpResponse
import cc.spray.http.MediaTypes
import cc.spray.io.IoWorker
import cc.spray.util.pimpFuture
import cc.spray.http.HttpMethod
import akka.dispatch.Future
import net.liftweb.json._

class RestClient(subsystem: String) {

  implicit val system = ActorSystem()
  def log = system.log

  val db = "default"
  val viewAll = "/_design/engine_instances/_view/all"
  val host = "localhost"
  val portDb = 5984
  val portIaaS = 8080

  val ioWorker = new IoWorker(system).start()
  val httpClient = system.actorOf(props = Props(new HttpClient(ioWorker)), name = "http-client")

  /**
   * Query the DB for the instance given. A flag is passed to determine
   * if the get operation is for the instance descriptor ("instance"), or
   * the current instance state ("state")
   */
  def get(instanceId: String, flag: String, keepOpen: Boolean = false): Either[String, String] = {
    var url: String = null

    //TODO Need to fix this to match subsytem and instance/state views
    if (flag == "instance")
      url = createUrl() + instanceId
    else
      url = createUrl() + instanceId //Change to state URL...

    callRemote(HttpMethods.GET, url, portDb, null, keepOpen)
  }

  def getAll(flag: String): Either[String, String] = {
    //TODO Need to fix this to match subsytem and instance/vs. state views
    var url: String = null
    if (flag == "instance")
      url = createUrl() + viewAll
    else
      url = createUrl() + viewAll //Change to state URL...

    callRemote(HttpMethods.GET, url, portDb, null)
  }

  /**
   * Put a new document in the DB. Must give it an ID
   */
  def put(file: String, id: String): Either[String, String] = {

    val contentType = new ContentType(MediaTypes.`application/json`, Some(HttpCharsets.`UTF-8`))
    val url = createUrl() + id
    val content = Some(HttpContent(contentType, file))

    callRemote(HttpMethods.PUT, url, portDb, content)
  }

  /**
   * Put a new document in the DB and let the DB generate the id
   */
  def post(payload: String): Either[String, String] = {

    val contentType = new ContentType(MediaTypes.`application/json`, Some(HttpCharsets.`UTF-8`))
    val url = createUrl()
    val content = Some(HttpContent(contentType, payload))

    callRemote(HttpMethods.POST, url, portDb, content)
  }

  /**
   * Delete the instance with the given ID
   */
  def delete(id: String): Either[String, String] = {
    //1. Get the instance from the DB using the instanceId
    val response = get(id, "instance", true)

    //2. Get the rev number from the doc
    var rev: String = null
    response match {
      case Right(result) => {
        val json = parse(result)
        rev = (json \ "_rev").values.toString()
      }
      case Left(error) => println(error)
    }

    println("Deleting Instance " + id + " with rev: " + rev)

    //3. Delete the document
    val url = createUrl() + id + "?rev=" + rev
    callRemote(HttpMethods.DELETE, url, portDb, null);
  }

  def execute(instance: String, payload: String): Either[String, String] = {
    val url = "/" + subsystem + "/" + instance // + "/execute"

    val contentType = new ContentType(MediaTypes.`application/json`, Some(HttpCharsets.`UTF-8`))
    val content = Some(HttpContent(contentType, payload))

    callRemote(HttpMethods.POST, url, portIaaS, content)
  }

  def callRemote(httpMethod: HttpMethod, url: String, port: Int, content: Some[HttpContent], keepOpen: Boolean = false): Either[String, String] = {

    val conduit = new HttpConduit(httpClient, host, port)

    try {
      log.info("Attempting to call server at url: " + host + ":" + port + url)
      var responseFuture: Future[HttpResponse] = null;
      if (content != null)
        responseFuture = conduit.sendReceive(HttpRequest(method = httpMethod, uri = url).withContent(content))
      else
        responseFuture = conduit.sendReceive(HttpRequest(method = httpMethod, uri = url))

      val response = responseFuture.await

      show(response)

      if (!keepOpen) {
        close(conduit)
      }

      response.content match {
        case Some(content) => Right(new String(content.buffer))
        case None => Left("Error getting instance from DB")
      }
    } catch {
      case e: Exception => {
        close(conduit)
        println(e.printStackTrace())
        Left(e.getMessage())

      }
    }
  }

  def close(conduit: HttpConduit) = {
    conduit.close()
    system.shutdown()
    ioWorker.stop()
  }

  def show(response: HttpResponse) = {
    log.info(
      """|Response for request:
         |status : {}
         |headers: {}
         |body : {}""".stripMargin,
      response.status.value, response.headers.mkString("\n ", "\n ", ""),
      response.content)
  }

  def createUrl(): String = {
    //TODO Set depending on the view to use for each subsystem
    var url: String = null

    if (subsystem == "engines")
      url = "/" + db + "/"
    else
      url = "/" + db + "/"
    url
  }
}