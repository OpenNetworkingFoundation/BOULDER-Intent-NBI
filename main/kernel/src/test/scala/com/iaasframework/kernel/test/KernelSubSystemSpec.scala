package com.iaasframework.kernel.test

import org.scalatest.BeforeAndAfterAll
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.junit.ShouldMatchersForJUnit
import akka.actor._
import com.iaasframework.kernel._

@RunWith(classOf[JUnitRunner])
class KernelSubSystemSpec extends FlatSpec with ShouldMatchersForJUnit with BeforeAndAfterAll {

  "The Kernel Service" should "be instanciated and started" in {}
  "The Kernel Service" should "be initizaled from a Config snippet" in {
    //   Thread.sleep(3600*30)
  }

  it should "delete an engine instance" in (pending)

}
