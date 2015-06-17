package org.openintent.devkit

object HelpJob extends Job {
  def getJobName = "Help"
  def getJobDescription = "Provide information about a target"
  def executeJob(args: Map[String, String]) = {}
}
