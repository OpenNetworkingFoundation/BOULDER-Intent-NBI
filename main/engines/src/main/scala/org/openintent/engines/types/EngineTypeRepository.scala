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
package org.openintent.engines

import akka.actor.Actor
import scouch.db._
import dispatch._
import dispatch.json._
import dispatch.json.JsHttp._
import Options._
import BulkDocument._
import akka.actor.ActorRef
import sjson.json._
import EngineTypeProtocol._
import JsonSerialization._

/**
 */
sealed trait EngineTypeRepositoryMessage
case class SaveEngineType(engType: EngineType) extends EngineTypeRepositoryMessage
case class LoadEngineTypeById(engTypeId: String) extends EngineTypeRepositoryMessage
case class LoadEngineTypeByName(engTypeName: String) extends EngineTypeRepositoryMessage

trait EngineTypeRepository extends Actor

/**
 * Engine Type Storage for CouchDB databases
 */
class CouchdbEngineTypeRepository extends EngineTypeRepository {

  // specification of the db server running
  val db = Db(Couch(), "engine_types")
  val http = new Http with thread.Safety
  try {
    http(db create)
  } catch {
    case e: dispatch.StatusCode ⇒
      e.code
  }
  /**
   * Message Handling Method
   */
  def receive = {
    case SaveEngineType(engType: EngineType) ⇒
      val id = saveType(engType)
      sender ! id
    case LoadEngineTypeById(engTypeId: String) ⇒

  }

  /**
   * Create a new document if it doesn't exist
   * @param engType The EngineType Metadata class
   */
  def saveType(engType: EngineType): String = {
    val engTypejson = tojson[EngineType](engType)
    val id = http(db doc engTypejson)
    id._1
  }
  /**
   * Query an EngineType by typeName
   * @param typeName Name of the EngineType
   */
  def queryType(typeName: String): EngineType = {
    val eng = http(db.get[EngineType](typeName))
    eng._3
  }

  /**
   * Update an existing document type
   * @param engType New Version of the EngineType Document
   */
  def updateType(engType: EngineType) = {
    val ref = http(db getRef engType.name)
    http(Doc(db, ref._1) update (engType, ref._2))
  }

}
/**
 * Factory Trait used to Mixin the Couchdb storage actor for Engine Types
 */
trait CouchdbEngineTypeRepositoryFactory { this: Actor ⇒
  //val repository: ActorRef = this.self.spawnLink[CouchdbEngineTypeRepository] // starts and links EngineTypeStorage
}
