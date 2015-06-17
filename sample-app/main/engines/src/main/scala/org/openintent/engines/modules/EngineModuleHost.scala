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
import org.mozilla.javascript._
import akka.actor._

class EngineModuleHost extends ScriptableObject {

  // Method jsConstructor defines the JavaScript constructor
  def jsConstructor() = {}

  // The class name is defined by the getClassName method
  @Override
  def getClassName(): String = { "EngineModule" }

}
