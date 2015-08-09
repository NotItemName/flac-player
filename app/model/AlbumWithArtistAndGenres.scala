package model

import play.api.libs.json.Json


case class AlbumWithArtistAndGenres(id: Option[Int], name: String, year: Int,
                                    artist: String, genres: Seq[String])

object AlbumWithArtistAndGenres {
  implicit val jsonFormat = Json.format[AlbumWithArtistAndGenres]
}