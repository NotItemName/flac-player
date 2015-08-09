package model

import play.api.libs.json.Json


case class AlbumWithArtistAndGenres(id: Option[Int], name: String, year: Int,
                                    artist: String, genre: Seq[String])

object AlbumWithArtistAndGenres {
  implicit val jsonFormat = Json.format[AlbumWithArtistAndGenres]
}