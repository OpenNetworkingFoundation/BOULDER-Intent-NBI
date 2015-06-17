package org.openintent.devkit
import java.io.File

object Main {
  val jobs = List[Job](RunJob, CompileJob, PackageJob, PublishJob, HelpJob)

  def usage = {
    var usage = """
Usage (optionals marked with *): 
devkit [environment]* [target] [arguments]*
   
Examples
  devkit create
  devkit run
    
Available Targets (type devkit help 'target-name' for more info):
"""
    jobs.foreach(job => usage += "  " + job.getJobName + "                    ".substring(job.getJobName.length()) + job.getJobDescription + "\n")
    usage
  }
  val currDir = new File(".").getAbsolutePath()
  val arguments = Map("currentDirectory" -> currDir)
  def main(args: Array[String]) {
    if (args.isEmpty)
      println(usage)
    else
      args.foreach(executeJob =>
        jobs.foreach(job => if (executeJob.equalsIgnoreCase(job.getJobName)) job.executeJob(arguments)))
  }
}
