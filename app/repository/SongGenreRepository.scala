package repository

import model.{Song, Genre}
import table.Tables._
import table.Tables.dbConfig.driver.api._


class SongGenreRepository {

  def save(song: Song, genres: Seq[Genre]): DBIO[Option[Int]] =
    songGenreTable ++= genres.map(genre => (song.id.get, genre.id.get))


}
