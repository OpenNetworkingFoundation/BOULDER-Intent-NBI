package org.openintent.devkit
import org.mozilla.javascript._
import java.io._
import org.mozilla.javascript.tools.shell.Global
import com.iaasframework.kernel.plugins.PluginProtocol._
import org.slf4j.LoggerFactory
import jline.console.ConsoleReader
import jline.console.completer.StringsCompleter
/**
 * The shell program.
 *
 * Can execute scripts interactively or in batch mode at the command line.
 * An example of controlling the JavaScript engine.
 *
 * @author Mathieu Lemay
 */

class InteractiveShell(val cx: Context, val plugin: Plugin) extends ScriptableObject {
  val main = cx.initStandardObjects();
  var currentContext: Scriptable = main;
  val names = Array[String]("quit", "log", "list", "exec", "reload", "select", "run", "help")
  main.defineFunctionProperties(names, classOf[InteractiveShell], ScriptableObject.DONTENUM)
  val contextLoader = new ContextLoader(cx, main, plugin);

  @Override
  def getClassName(): String =
    {
      return "global";
    }

  /**
   * Print the string values of its arguments.
   *
   * This method is defined as a JavaScript function.
   * Note that its arguments are of the "varargs" form, which
   * allows it to handle an arbitrary number of arguments
   * supplied to the JavaScript function.
   *
   */
  def print(cx: Context, thisObj: Scriptable, args: Array[Object], funObj: Function) =
    {
      args.toList.foreach { arg =>
        // Convert the arbitrary JavaScript value into a string form.
        val s = Context.toString(arg);
        System.out.print(s);
        s
      }
      System.out.println();
    }

  /**
   * Start an interactive shell
   *
   * @param cx the current context
   * @param scope , scope of the current shell
   */
  def start() =
    {
      val reader = new ConsoleReader(null, System.in, System.out, null)
      import scala.collection.JavaConverters._
      reader.addCompleter(new StringsCompleter(names.toSeq.asJava))
      val sourceName = "<stdin>"
      InteractiveShell.quitting = false;
      var hitEOF = false;
      var lineno = 1;

      Stream.continually(reader.readLine()).takeWhile(_ != null && !hitEOF) foreach { line =>
        reader.setPrompt(InteractiveShell.path)
        System.err.flush();
        try {
          var source = "";
          // Collect lines of source to compile.
          val startline = lineno;
          var collecting = true
          while (collecting) {
            val newline = line
            if (newline == null) {
              hitEOF = true;
              collecting = false;
            }
            source = source + newline + "\n";
            lineno += 1
            // Continue collecting as long as more lines
            // are needed to complete the current
            // statement.  stringIsCompilableUnit is also
            // true if the source statement will result in
            // any error other than one that might be
            // resolved by appending more source.
            if (cx.stringIsCompilableUnit(source))
              collecting = false;
          }

          val result = cx.evaluateString(currentContext, source,
            sourceName, startline,
            null);
          if (result != Context.getUndefinedValue()) {
            System.err.println(Context.toString(result));
          }
        } catch {
          case we: WrappedException =>
            // Some form of exception was caught by JavaScript and
            // propagated up.
            System.err.println(we.getWrappedException().toString());
            we.printStackTrace();
          case ee: EvaluatorException =>
            // Some form of JavaScript error.h
            System.err.println("js: " + ee.getMessage());
          case jse: JavaScriptException =>
            // Some form of JavaScript error.
            System.err.println("js: " + jse.getMessage());
          case ioe: IOException =>
            System.err.println(ioe.toString());
          case e: Exception =>
            System.err.println(e.getMessage());
        }
      }
    }
}
object InteractiveShell {
  var quitting = false;
  var path = "> ";
  var shell: InteractiveShell = null;
  val logger = LoggerFactory.getLogger(classOf[InteractiveShell]);

  def create(cx: Context, plugin: Plugin): InteractiveShell = {
    shell = new InteractiveShell(cx, plugin)
    shell
  }
  /**
   * Print the string values of its arguments.
   *
   * This method is defined as a JavaScript function.
   * Note that its arguments are of the "varargs" form, which
   * allows it to handle an arbitrary number of arguments
   * supplied to the JavaScript function.
   *
   */
  def select(arg: String) =
    {
      val context = shell.contextLoader.getContext(arg)
      context match {
        case Some(scope) =>
          shell.currentContext = scope
          quit
          shell.start()
        case None =>
      }
      path = arg + "> "
    }

  /**
   * List all the available contexts
   */
  def list() =
    {
      println(shell.contextLoader.listContext());
    }

  /**
   * Print the string values of its arguments.
   *
   * This method is defined as a JavaScript function.
   * Note that its arguments are of the "varargs" form, which
   * allows it to handle an arbitrary number of arguments
   * supplied to the JavaScript function.
   *
   */
  def run(cx: Context, thisObj: Scriptable, args: Array[Object], funObj: Function) =
    {
      args.toList.foreach { arg =>
        // Convert the arbitrary JavaScript value into a string form.
        val s = Context.toString(arg);
        System.out.print(s);
        val file = new File(s)
        if (file.exists())
          processSource(cx, shell.currentContext, s)
        else {
          println("File not found:" + s)
        }
      }
      System.out.println();
    }

  /**
   * Print the string values of its arguments.
   *
   * This method is defined as a JavaScript function.
   * Note that its arguments are of the "varargs" form, which
   * allows it to handle an arbitrary number of arguments
   * supplied to the JavaScript function.
   *
   */
  def exec(script: String) =
    {
      val file = new File(script)
      if (file.exists())
        processSource(shell.cx, shell.currentContext, script)
      else {
        println("File not found:" + script)
      }
      System.out.println();
    }
  /**
   * Quit the shell.
   *
   * This only affects the interactive mode.
   *
   * This method is defined as a JavaScript function.
   */
  def quit() =
    {
      quitting = true;
    }
  /**
   * Quit the shell.
   *
   * This only affects the interactive mode.
   *
   * This method is defined as a JavaScript function.
   */
  def reload() =
    {
      shell.currentContext = shell.main;
      quitting = true
      shell.start
    }
  /**
   *
   */
  def log(logLevel: String, logMessage: String) =
    {
      logLevel match {
        case "debug" =>
          logger.debug(logMessage)
        case "info" =>
          logger.info(logMessage)
        case "warn" =>
          logger.warn(logMessage)
      }

    }

  def processSource(cx: Context, scope: Scriptable, filename: String) =
    {
      val in = new FileReader(filename);
      try {
        cx.evaluateReader(scope, in, filename, 1, null);
      } catch {
        case we: WrappedException =>
          System.err.println(we.getWrappedException().toString());
          we.printStackTrace();
        case ee: EvaluatorException =>
          System.err.println("js: " + ee.getMessage());
        case jse: JavaScriptException =>
          System.err.println("js: " + jse.getMessage());
        case ioe: IOException =>
          System.err.println(ioe.toString());
        case ex: FileNotFoundException =>
          Context.reportError("Couldn't open file \"" + filename + "\".");

      } finally {
        in.close();
      }

    }

  /**
   * Print a help message.
   *
   * This method is defined as a JavaScript function.
   */
  def help(cx: Context, thisObj: Scriptable, args: Array[Object], funObj: Function) = {
    println("""
        Command                Description
        =======                ===========
        help()                 Display usage and help messages. 
        run(file.js)           Run an external script.
        exec(file.js)          Execute a subscript.
        list()                 List available contexts.
        reload()               Reloads the current context and cleans up shell.
        select(context)        Switch the current context to the specified argument.
        quit()                 Quit the shell. 
     """)
  }

}
