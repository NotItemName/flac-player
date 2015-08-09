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
    for {
      tuple <- findAllAlbumsWithArtistAndGenres(from, limit)
    } yield for {
      ((album, artistName), genres) <- tuple
    } yield AlbumWithArtistAndGenres(album.id, album.name, album.year, artistName, genres)
  }

  private def findAllAlbumsWithArtistAndGenres(from: Int, limit: Int): Future[Seq[((Album, String), Seq[String])]] = {
    db.run {
      for {
        albumsWithArtist <- albumRepository.findAll(from, limit)
        result <- DBIO.sequence(
          albumsWithArtist.map { albumTuple =>
            genreRepository.findByAlbumId(albumTuple._1.id.get).map { genres =>
              (albumTuple, genres.map(_.name))
            }
          }
        )
      } yield result
    }
  }

  def findById(id: Int): Future[Option[AlbumWithArtistAndGenres]] = {
    for {
      tuple <- findByIdAlbumsWithArtistAndGenres(id)
    } yield for {
      ((album, artistName), genres) <- tuple
    } yield AlbumWithArtistAndGenres(album.id, album.name, album.year, artistName, genres)
  }

  private def findByIdAlbumsWithArtistAndGenres(id: Int): Future[Option[((Album, String), Seq[String])]] = {
    val query: DBIO[Option[((Album, String), Seq[String])]] =
      albumRepository.findById(id).flatMap {
        case Some((album, artist)) => genreRepository.findByAlbumId(album.id.get).map { x =>
          Some(((album, artist), x.map(_.name)))
        }
        case None => DBIO.successful(None)
      }
    db.run(query)
  }

}
