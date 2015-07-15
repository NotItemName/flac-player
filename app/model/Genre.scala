package model

import play.api.libs.json.Json


case class Genre(id: Option[Int], name: String)

object Genre {
  implicit val jsonFormat = Json.format[Genre]
}