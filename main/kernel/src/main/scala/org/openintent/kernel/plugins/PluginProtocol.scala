package org.openintent.kernel.plugins
import dispatch.json._
import sjson.json._
import JsonSerialization._
import scala.reflect.BeanInfo

object PluginProtocol extends DefaultProtocol {

  @BeanInfo
  case class Plugin(
    doctype: String,
    info: Info,
    extensions: List[Extension])

  @BeanInfo
  case class Author(
    name: String,
    email: String)

  @BeanInfo
  case class Info(
    name: String,
    version: String,
    summary: String,
    description: String,
    homepage: String,
    license: String,
    authors: List[Author])

  @BeanInfo
  case class Extension(
    name: String,
    system: String,
    file: String)

  implicit val ExtensionFormat: Format[Extension] =
    asProduct3("name", "system", "file")(Extension)(Extension.unapply(_).get)

  implicit val AuthorFormat: Format[Author] =
    asProduct2("name", "email")(Author)(Author.unapply(_).get)

  implicit val InfoFormat: Format[Info] =
    asProduct7("name", "version", "summary", "description", "homepage", "license", "authors")(Info)(Info.unapply(_).get)

  implicit val PluginFormat: Format[Plugin] =
    asProduct3("doctype", "info", "extensions")(Plugin)(Plugin.unapply(_).get)

}
