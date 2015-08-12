package view

import model.{Album, Artist, Song}
import play.api.libs.json.Json


case class SongWithAlbumArtistAndGenres(id: Option[Int], name: String, year: Int, trackNumber: Int,
                                        artist: String, album: String, genres: Seq[String])

object SongWithAlbumArtistAndGenres {
  implicit val jsonFormat = Json.format[SongWithAlbumArtistAndGenres]

  val transform: ((Song, Album, Artist, Seq[String])) => SongWithAlbumArtistAndGenres =
    (tuple) => SongWithAlbumArtistAndGenres(tuple._1.id, tuple._1.name, tuple._2.year,
      tuple._1.trackNumber, tuple._3.name, tuple._2.name,tuple._4)
}

//
//"id":1,
//"name":"Do I Wanna Know?",
//"year":2014,
//"genres":["Indie"],
//"artist_name":"Arctic Monkeys",
//"album_name":"AM",
//"track_number":1