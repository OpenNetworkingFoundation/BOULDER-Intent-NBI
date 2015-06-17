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
package org.openintent.engines.test
import org.scalatest.BeforeAndAfterAll
import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.junit.ShouldMatchersForJUnit
import akka.actor._
import javax.management.modelmbean.ModelMBeanAttributeInfo
import akka.actor.Actor._
import com.iaasframework.engines.types._
import akka.testkit.TestActorRef
import EngineTypeProtocol._
import sjson.json._
import JsonSerialization._
import dispatch.json._

@RunWith(classOf[JUnitRunner])
class EngineTypeProtocolSpec extends FlatSpec with ShouldMatchersForJUnit with BeforeAndAfterAll {
  val sampleType = EngineType("SampleEngine", "id ", "desc", "1.0.0", Map("sampleParam1" -> "value1", "sampleParam2" -> "value2"),
    List(EngineModuleInfo("moduleName1", "value1")))
  val sampleTypeString = """
{"globalSettings" : {"sampleParam1" : "value1", "sampleParam2" : "value2"}, "name" : "SampleEngine", "modules" : [{"name" : "moduleName1", "settings" : {"param1" : "value1"}}], "description" : "desc", "version" : "1.0.0"}
       """;
  "The EngineTypeProtocol" should "serialize EngineType metadata objects to a JSON string" in {
    val json = tojson[EngineType](sampleType)
    assertEquals(json, Js(sampleTypeString));
  }

  it should "unserialize a JSON string representing EngineType metadata to a set of scala objects" in {
    val obj = fromjson[EngineType](Js(sampleTypeString))
    assertEquals(obj, sampleType);
  }
}
