package org.openintent.kernel
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * Kernel base Exception. Each Exception gets:
 * <ul>
 * <li>toString that includes exception name, message and the stacktrace</li>
 * </ul>
 * @author Mathieu Lemay (IT)
 */
class KernelException(message: String = "", cause: Throwable = null) extends RuntimeException(message, cause) with Serializable {

  override lazy val toString =
    "%s: %s\n[%s]\n%s".format(getClass.getName, message, stackTraceToString)

  def stackTraceToString = {
    val trace = getStackTrace
    val sb = new StringBuffer
    for (i ← 0 until trace.length)
      sb.append("\tat %s\n" format trace(i))
    sb.toString
  }
}

