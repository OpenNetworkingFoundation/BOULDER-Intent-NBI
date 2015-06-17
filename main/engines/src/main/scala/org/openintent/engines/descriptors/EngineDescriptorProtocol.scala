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
case class EngineDescriptor(
  name: String,
  typeId: String,
  description: String,
  settings: Map[String, String] = Map.empty,
  modules: List[ModuleDescriptor] = List())

case class ModuleDescriptor(
  name: String,
  moduleType: String,
  enabled: Boolean,
  settings: Map[String, String] = Map.empty)

object EngineDescriptorProtocol extends DefaultProtocol {

  implicit val EngineModuleInfoFormat: Format[ModuleDescriptor] =
    asProduct4("name", "moduleType", "enabled", "settings")(ModuleDescriptor)(ModuleDescriptor.unapply(_).get);

  implicit val EngineTypeFormat: Format[EngineDescriptor] =
    asProduct5("name", "typeId", "description", "settings", "modules")(EngineDescriptor)(EngineDescriptor.unapply(_).get)

}
