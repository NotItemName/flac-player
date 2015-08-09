package service

import javax.inject.Inject

import model._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository.{AlbumRepository, GenreRepository}
import slick.dbio.DBIO
import table.Tables.dbConfig._

import scala.concurrent.Future

class AlbumService @Inject()(private val albumRepository: AlbumRepository,
                             private val genreRepository: GenreRepository) {

  def findAll(from: Int, limit: Int): Future[Seq[AlbumWithArtistAndGenres]] = {
    val query =
      albumRepository.findAllAlbumsWithArtist(from, limit).flatMap { albumsWithArtist =>
        DBIO.sequence(
          albumsWithArtist.map { case (album, artist) => findGenresForAlbum(album, artist) }
        )
      }

    db.run(query)
  }

  def findById(id: Int): Future[Option[AlbumWithArtistAndGenres]] = {
    val query =
      albumRepository.findByIdAlbumWithArtist(id).flatMap {
        case Some((album, artist)) => findGenresForAlbum(album, artist).map(Some(_))
        case None => DBIO.successful(None)
      }

    db.run(query)
  }


  private def findGenresForAlbum(album: Album, artist: String): DBIO[AlbumWithArtistAndGenres] = {
    genreRepository.findByAlbumId(album.id.get).map { genres =>
      AlbumWithArtistAndGenres(album.id, album.name, album.year, artist, genres.map(_.name))
    }
  }

}
