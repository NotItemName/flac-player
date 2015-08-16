package view

import model.{Genre, Album}
import play.api.libs.json.Json


case class AlbumWithArtistAndGenres(id: Option[Int], name: String, year: Int,
                                    artist: String, genres: Seq[String])

object AlbumWithArtistAndGenres {
  implicit val jsonFormat = Json.format[AlbumWithArtistAndGenres]

  def transform(tuple: (Album, String, Seq[String])):AlbumWithArtistAndGenres = {
    AlbumWithArtistAndGenres(tuple._1.id, tuple._1.name, tuple._1.year, tuple._2, tuple._3)
  }

}