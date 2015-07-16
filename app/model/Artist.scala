package model

import play.api.libs.json.Json

/**
 * @author Mykola Zalyayev
 */
case class Artist(id:Option[Int], name:String)

object Artist {
  implicit val jsonFormat = Json.format[Artist]
}
