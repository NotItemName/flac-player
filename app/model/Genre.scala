package model

import play.api.libs.json.Json


case class Genre(name: String, id: Option[Int] = None) extends Model

object Genre {
  implicit val jsonFormat = Json.format[Genre]
}