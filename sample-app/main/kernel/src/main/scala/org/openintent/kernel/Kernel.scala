package org.openintent.kernel
import java.util.Date
import javax.management.ObjectName
import javax.management.modelmbean.ModelMBeanAttributeInfo
import scala.reflect.BeanProperty
import scala.collection.mutable.Map
import akka.actor.Actor
import akka.actor.Actor._
import akka.event.Logging
import akka.actor.ActorRef
import scala.collection.mutable.HashMap

/** Kernel Management Messages **/
sealed trait KernelManagementMessage extends Serializable
case class InitializeKernel() extends KernelManagementMessage
case class KernelInfoRequest() extends KernelManagementMessage
case class KernelInfoResponse(info: KernelInfo) extends KernelManagementMessage
case class ShutdownKernel() extends KernelManagementMessage
case class GetKernelSubSystem(name: String) extends KernelManagementMessage

/**
 * Kernel Trait provides everything needed to transform an actor as a kernel.
 * @author <a href="http://jonasboner.com">Inocybe Technologies inc.</a>
 *
 */
trait Kernel extends Actor {
  /** Description for the JMX Resource **/
  val description = "Kernel Service"

  /** Kernel Services **/
  val kernelSubSystems: HashMap[String, ActorRef] = new HashMap[String, ActorRef]

  /** Kernel Start Time **/
  val startTime = System.currentTimeMillis

  val objectNameString = "ca.inocybe.platform:type=Kernel"

  /** Kernel Uptime **/
  def uptime = (System.currentTimeMillis - startTime) / 1000

  /** Initialization Callback **/
  protected def initialize() = {}

  /** Shutdown Callback **/
  protected def shutdown() = {}

  val log: akka.event.LoggingAdapter

  protected def kernelReceive: Receive = {
    // Kernel Initialization sent after Launching
    case InitializeKernel() ⇒
      log.info("Initializing Kernel")
      initialize

    // Kernel Information Request
    case KernelInfoRequest ⇒
      sender ! KernelInfoResponse(generateInfo)
      log.info("Received Kernel Info Request")

    case GetKernelSubSystem(name: String) ⇒
      sender ! kernelSubSystems.get(name)

    case ShutdownKernel ⇒
      //     kernelSubSystems.foreach( service => service._2 ! KernelStopped(Actor.actorOf(this)))
      shutdown
      log.info("Shutting down Kernel")

    case _@ msg ⇒
      dispatchSubSystems(msg)

  }

  private def generateInfo: KernelInfo = {
    val info = KernelInfo(uptime.toString, "2.0.0", "IaaS Framework", "localhost")
    info
  }
  private def dispatchSubSystems(msg: Any) = {
    kernelSubSystems.foreach { subsystem ⇒
      subsystem._2 forward msg
    }
  }
}
/**
 * Kernel Companion Object
 */
object Kernel {
  var kernel: Option[ActorRef] = None

  def boot(kernelRef: ActorRef) = {
    kernel = Some(kernelRef)
  }

  def getSubSystem(name: String): Option[ActorRef] = {
    // val result = (kernel.get ? GetKernelSubSystem(name)).as[ActorRef]
    // result
    None
  }
}
