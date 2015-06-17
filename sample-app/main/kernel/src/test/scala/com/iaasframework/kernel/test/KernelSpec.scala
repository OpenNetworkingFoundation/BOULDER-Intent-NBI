package com.iaasframework.kernel.test

import org.scalatest.BeforeAndAfterAll
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.junit.ShouldMatchersForJUnit
import akka.actor._
import com.iaasframework.kernel._
import scala.collection.mutable.Map
import akka.actor.Actor._
import scala.reflect.BeanProperty

@RunWith(classOf[JUnitRunner])
class KernelSpec extends FlatSpec with ShouldMatchersForJUnit with BeforeAndAfterAll {

  "The Kernel" should "be initialized with configuration on startup" in (pending) //    kernel ! InitializeKernel(config)

  it should "list all the available kernel subsystems" in (pending)

  it should "be capable of handling kernel information requests" in (pending)

  it should "send kernel events to registered subsystems" in (pending)

}
