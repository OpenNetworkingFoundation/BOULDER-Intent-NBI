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
package org.openintent.kernel

import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import akka.util.Duration
import com.typesafe.config.Config
import java.util.concurrent.TimeUnit

class KernelSettingsImpl(config: Config) extends Extension {
  val DbUri: String = config.getString("myapp.db.uri")
  // val CircuitBreakerTimeout: Duration = Duration(config.getMilliseconds("myapp.circuit-breaker.timeout"), TimeUnit.MILLISECONDS)
}

object KernelSettings extends ExtensionId[KernelSettingsImpl] with ExtensionIdProvider {

  override def lookup = KernelSettings

  override def createExtension(system: ExtendedActorSystem) = new KernelSettingsImpl(system.settings.config)
}
