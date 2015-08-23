package service

import com.google.inject.Inject
import model.{Album, Artist, Song}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository._
import slick.dbio.DBIO
import table.Tables.dbConfig._
import table.Tables.dbConfig.driver.api._
import view.SongWithAlbumArtistAndGenres

import scala.concurrent.Future

class SongService @Inject()(private val songRepository: SongRepository,
                            private val artistRepository: ArtistRepository,
                            private val albumRepository: AlbumRepository,
                            private val songGenreRepository: SongGenreRepository,
                            private val genreRepository: GenreRepository) {
  def save(song: SongWithAlbumArtistAndGenres, fileName: String): Future[Song] = {
    val query: DBIO[Song] = artistRepository.findByName(song.artist).flatMap {
      case None => DBIO.failed(new Exception("Artist doesn't exist"))
      case Some(artist) => saveSongWithGenres(song, fileName, artist)
    }
    db.run(query.transactionally)
  }

  private def saveSongWithGenres(fullSong: SongWithAlbumArtistAndGenres, fileName: String, artist: Artist): DBIO[Song] = {
    albumRepository.findByName(fullSong.album).flatMap {
      case None => DBIO.failed(new Exception("Album doesn't exist"))
      case Some(album) => genreRepository.saveNotExistedGenres(fullSong.genres).flatMap { genres =>
        println(album)
        println(artist)
        songRepository.save(Song(fullSong.name, fullSong.trackNumber, fileName, artist.id.get, album.id.get)).flatMap(song =>
          songGenreRepository.save(song, genres).map(x=>song)
        )
      }
    }
  }


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