package org.openintent.client

/**
 * Intent Engine Client
 */
object Client {

  def main(args: Array[String]) {
    if (args.length == 0) {
      printUsage()
    } else {
      //remove the command name from the arguments list
      val commandArgs = new Array[String](args.length - 1)
      for (i <- 1 to args.length - 1)
        commandArgs(i - 1) = args(i)

      args(0) match {
        case "create" => val command = new Create; command.execute(commandArgs)
        case "delete" => val command = new Delete; command.execute(commandArgs)
        case "get" => val command = new Get; command.execute(commandArgs)
        case "update" => val command = new Update; command.execute(commandArgs)
        case "execute" => val command = new Execute; command.execute(commandArgs)
        case _ => printUsage()
      }
    }
  }

  def printUsage() = println("""|invalid option
                           |
                           |USAGE: client <command> [OPTIONS] <subsystem> <parameters>
                           |
                           | To see the specific usage for each command, execute the command without any 
		  				   | options or parameters
                           |
                           |Commands: 
                           |  get        Get an instance from the DB. Depending on the flag, this can get 
		  				   |             the instance descriptor, or the instance state. use "ALL" as the 
                           |             instance ID and it will get All instances
                           |            
                           |  create     Create a new instance from a provided instance file
                           |
                           |  delete     Delete an instance            
                           |
                           |  update     Update an instance. Instances can be updated by providing a new 
                           |             instance file, or by individual name/value pairs
                           |            
                           |  execute    Execute an operation on an instance
                           |
                           |Subsystems: 
                           |  engines     Use the engine subsystem
                           |
                           |  resources   Use the resource subsystem
                           |""".stripMargin)

}
