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
import akka.actor._
import scala.reflect.BeanInfo
import sjson.json.JSONTypeHint
import scala.annotation.target.field
import com.iaasframework.engines._
import EngineTypeProtocol._

sealed trait EngineTypeManagerMessage
case class CreateEngineType(desc: EngineType) extends EngineTypeManagerMessage
case class QueryEngineType(id: String) extends EngineTypeManagerMessage
case class DeleteEngineType(id: String) extends EngineTypeManagerMessage

class EngineTypeManager extends Actor with CouchdbEngineTypeRepositoryFactory {

  def receive = {
    case CreateEngineType(desc) ⇒

    case _ ⇒
    //    self forward repository
  }
}
