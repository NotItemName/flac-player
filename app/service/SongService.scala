package service

import com.google.inject.Inject
import model.{Album, Artist, Song}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository.{GenreRepository, SongRepository}
import slick.dbio.DBIO
import table.Tables.dbConfig._

import scala.concurrent.Future

class SongService @Inject()(private val songRepository: SongRepository,
                            private val genreRepository: GenreRepository) {

  def findFileNameById(id: Int): Future[Option[String]] = db.run(songRepository.findFileNameById(id))

  def delete(id: Int): Future[Int] = db.run(songRepository.delete(id))

  def findById(id: Int): Future[Option[(Song, Album, Artist, Seq[String])]] = {
    findGenresFor {
      songRepository.findByIdSongsWithArtistAndAlbum(id).map(_.toSeq)
    }.map(_.headOption)
  }

  private def findGenresFor(songQuery: DBIO[Seq[(Song, Album, Artist)]]): Future[Seq[(Song, Album, Artist, Seq[String])]] = {
    val query = songQuery.flatMap { songs =>
      DBIO.sequence(
        songs.map {
          case (song, album, artist) => genreRepository.findBySongId(song.id.get).map {
            genres => (song, album, artist, genres.map(_.name))
          }
        }
      )
    }
    db.run(query)
  }

  def findAll(from: Int, to: Int): Future[Seq[(Song, Album, Artist, Seq[String])]] = {
    findGenresFor {
      songRepository.findAllSongsWithArtistAndAlbum(from, to)
    }
  }
}