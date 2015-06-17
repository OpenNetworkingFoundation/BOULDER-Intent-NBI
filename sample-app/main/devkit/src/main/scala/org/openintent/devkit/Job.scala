package org.openintent.devkit

trait Job {
  def getJobName: String
  def getJobDescription: String
  // def getJobUsage: String
  def executeJob(args: Map[String, String]): Unit
}
