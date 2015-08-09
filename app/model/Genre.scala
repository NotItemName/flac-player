package model

import play.api.libs.json.Json


case class Genre(name: String, id: Option[Int] = None)

object Genre {
  implicit val jsonFormat = Json.format[Genre]
}