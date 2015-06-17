package com.iaasframework.kernel.test

import org.scalatest.BeforeAndAfterAll
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.junit.ShouldMatchersForJUnit
import akka.actor._
import com.iaasframework.kernel.plugins._
import javax.management.modelmbean.ModelMBeanAttributeInfo
import akka.actor.Actor._

@RunWith(classOf[JUnitRunner])
class PluginProcessorSpec extends FlatSpec with ShouldMatchersForJUnit with BeforeAndAfterAll {

  "The PluginProcessor" should "process the manifest" in {
    val settings = Map("source_directory" -> "src/data")
    val plugin = PluginProcessor.process(settings);
  }

}
