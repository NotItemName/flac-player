package repository

import model.{Album, Genre}
import table.Tables._
import table.Tables.dbConfig.driver.api._

class AlbumGenreRepository {

  def save(album: Album, genres: Seq[Genre]): DBIO[Option[Int]] =
    albumGenreTable ++= genres.map(genre => (album.id.get, genre.id.get))

}
