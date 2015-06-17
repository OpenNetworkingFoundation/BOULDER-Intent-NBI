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

import sjson.json._
import dispatch.json._
import JsonSerialization._

/**
 * The EngineTypeProtocol provides type conversion for SJSON
 * classes EngineTypeMetadata and others.
 */

object EngineTypeProtocol extends DefaultProtocol {

  case class EngineType(
    name: String,
    typeId: String,
    description: String,
    version: String,
    settings: Map[String, String] = Map.empty,
    modules: List[EngineModuleInfo] = List())

  case class EngineModuleInfo(
    name: String,
    moduleId: String,
    settings: Map[String, String] = Map.empty)

  implicit val EngineModuleInfoFormat: Format[EngineModuleInfo] =
    asProduct3("name", "moduleId", "settings")(EngineModuleInfo)(EngineModuleInfo.unapply(_).get);

  implicit val EngineTypeFormat: Format[EngineType] =
    asProduct6("name", "typeId", "description", "version", "settings", "modules")(EngineType)(EngineType.unapply(_).get)

}
