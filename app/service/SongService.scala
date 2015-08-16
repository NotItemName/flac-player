package service

import com.google.inject.Inject
import model.{Genre, Album, Artist, Song}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository.{GenreRepository, SongRepository}
import slick.dbio.DBIO
import table.Tables.dbConfig._

import scala.concurrent.Future

class SongService @Inject()(private val songRepository: SongRepository,
                            private val genreRepository: GenreRepository) {

  def delete(id: Int): Future[Int] = {
    db.run(songRepository.delete(id))
  }

  def findById(id: Int): Future[Option[(Song, Album, Artist, Seq[String])]] = {
    groupBy(songRepository.findByIdSongsWithArtistAlbumAndGenres(id).map(_.toSeq)).map(_.headOption)
  }

  def findAll(from: Int, to: Int): Future[Seq[(Song, Album, Artist, Seq[String])]] = {
    groupBy(songRepository.findAllSongsWithArtistAlbumAndGenre(from, to))
  }

  private def groupBy(query: DBIO[Seq[(Song, Album, Artist, Genre)]]): Future[Seq[(Song, Album, Artist, Seq[String])]] = {
    db.run(query).map { result =>
      result.groupBy(y => (y._1, y._2, y._3)).map {
        case (((song, album, artist), tail)) => (song, album, artist, tail.map(_._4.name))
      }.toSeq
    }
  }
}