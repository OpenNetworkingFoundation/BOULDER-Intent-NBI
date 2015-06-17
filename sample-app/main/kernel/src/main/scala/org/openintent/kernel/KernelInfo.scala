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
/**
 * Information about the Platform Server's Kernel
 */
import scala.xml.XML

case class KernelInfo(
    kernelUptime: String,
    kernelVersion: String,
    platformName: String,
    domainName: String) {
  def toXML = {
    <KernelInfo>
      <KernelUptime>{ kernelUptime }</KernelUptime>
      <KernelVersion>{ kernelVersion }</KernelVersion>
      <PlatformName>{ platformName }</PlatformName>
      <DomainName>{ domainName }</DomainName>
    </KernelInfo>
  }
  override def toString: String = {
    "Kernel Information:\n" +
      "- Uptime: " + kernelUptime + "\n" +
      "- Version: " + kernelVersion + "\n" +
      "- Platform Name: " + platformName + "\n" +
      "- Domain Name: " + domainName + "\n"
  }
}

object KernelInfo {
  def fromXML(xmlString: String): KernelInfo =
    {
      val node = XML.loadString(xmlString)
      new KernelInfo((node \ "KernelUptime").text, (node \ "KernelVersion").text, (node \ "PlatformName").text, (node \ "DomainName").text)
    }
}
