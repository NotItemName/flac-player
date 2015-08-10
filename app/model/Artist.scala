package model

import play.api.libs.json.Json

case class Artist(id:Option[Int], name:String) extends Model

object Artist {
  implicit val jsonFormat = Json.format[Artist]
}
