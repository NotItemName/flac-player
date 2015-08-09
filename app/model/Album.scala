package model

import play.api.libs.json.Json

case class Album(name: String, year: Int, artistId: Int, id: Option[Int] = None)

object Album {
  implicit val jsonFormat = Json.format[Album]
}