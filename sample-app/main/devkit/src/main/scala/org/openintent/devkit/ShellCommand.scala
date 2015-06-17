package org.openintent.devkit
import org.mozilla.javascript.ScriptableObject

trait ShellCommand extends ScriptableObject {
  protected def name: String
  protected def usage: String
  protected def function
  protected def getClassName = "global"
}
