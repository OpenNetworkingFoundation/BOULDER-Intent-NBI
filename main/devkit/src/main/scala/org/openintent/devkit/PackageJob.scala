package org.openintent.devkit

import org.mozilla.javascript._
import java.io._
import java.util.zip._
import org.apache.commons.io.IOUtils

object PackageJob extends Job {
  def getJobName = "Package"
  def getJobDescription = "Package the plugin for distribution"

  /**
   * Main entry point.
   *
   * Process arguments as would a normal Java program. Also
   * create a new Context and associate it with the current thread.
   * Then set up the execution environment and begin to
   * execute scripts.
   */
  def executeJob(args: Map[String, String]) = {
    println("Packaging plugin to plugin.zip")
    try {
      val inFolder = new File(".");
      val outFolder = new File("plugin.zip");
      val out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder)));
      var in: BufferedInputStream = null;
      val len = inFolder.getAbsolutePath().lastIndexOf(File.separator);
      val baseName = inFolder.getAbsolutePath().substring(0, len + 1);
      addFolderToZip(inFolder, out, baseName);
      out.close();
    } catch {
      case e: Exception =>
        e.printStackTrace();
    }
  }
  def addFolderToZip(folder: File, zip: ZipOutputStream, baseName: String): Unit = {
    val files = folder.listFiles();
    files.foreach(file =>
      {
        if (file.isDirectory()) {
          addFolderToZip(file, zip, baseName);
        } else {
          val name = file.getAbsolutePath().substring(baseName.length());
          val zipEntry = new ZipEntry(name);
          zip.putNextEntry(zipEntry);
          IOUtils.copy(new FileInputStream(file), zip);
          zip.closeEntry();
        }
      })
  }
}

