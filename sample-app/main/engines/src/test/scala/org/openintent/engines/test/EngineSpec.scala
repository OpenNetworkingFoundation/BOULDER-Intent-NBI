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
import org.scalatest.junit.JUnitRunner
import org.scalatest.junit.ShouldMatchersForJUnit
import akka.actor._
import akka.util.duration._
import javax.management.modelmbean.ModelMBeanAttributeInfo
import akka.actor.Actor._
import akka.actor.Props
import akka.actor.ActorSystem
import akka.actor.Actor
import akka.testkit.TestKit
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender
import akka.testkit.TestFSMRef
import javax.script.ScriptEngineManager
import scala.collection.JavaConverters._
import com.iaasframework.engines.Engine
import com.iaasframework.engines.Idle
@RunWith(classOf[JUnitRunner])
class EngineSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpec with MustMatchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("MySpec"))

  override def afterAll {
    system.shutdown()
  }

  val fsm = TestFSMRef(new Engine(null))
  "engine actor" must {
    "blah blah" in {
      assert(fsm.stateName == Idle)
    
      val mgr = new ScriptEngineManager();
      val factories =
        mgr.getEngineFactories();
      factories.asScala.foreach(factory => {
        System.out.println("ScriptEngineFactory Info");
        val engName = factory.getEngineName();
        val engVersion = factory.getEngineVersion();
        val langName = factory.getLanguageName();
        val langVersion = factory.getLanguageVersion();
        System.out.printf("\tScript Engine: %s (%s)\n",
          engName, engVersion);
      })
     }
  }
}
