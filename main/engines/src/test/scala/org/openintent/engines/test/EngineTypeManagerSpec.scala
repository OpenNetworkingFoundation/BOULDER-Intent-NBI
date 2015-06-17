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
import org.scalatest.FlatSpec
import org.scalatest.junit.ShouldMatchersForJUnit
import akka.actor._
import javax.management.modelmbean.ModelMBeanAttributeInfo
import akka.actor.Actor._
import com.iaasframework.engines.types._
import akka.testkit.TestActorRef

@RunWith(classOf[JUnitRunner])
class EngineTypeManagerSpec extends FlatSpec with ShouldMatchersForJUnit with BeforeAndAfterAll {
  /**
   * val actorRef = TestActorRef(new Actor with EngineTypeManager { def receive = { case _ ⇒ } }).start()
   * val actor = actorRef.underlyingActor
   *
   * "The EngineTypeManager" should "allow creation of new types from JSON file" in {
   *
   * actor.create()
   * }
   *
   * it should "list all the available engine types" in {
   *
   * }
   *
   * it should "be capable of handling kernel information requests" in (pending)
   * it should "send kernel events to registered subsystems" in (pending)
   */
}
