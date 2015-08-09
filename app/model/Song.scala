package model

import play.api.libs.json.Json


case class Song(name: String, trackNumber: String, fileName: String,
                artistId: Int, albumId: Int, id: Option[Int])

object Song{
  implicit val jsonFormat = Json.format[Song]
}