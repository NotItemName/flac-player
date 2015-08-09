package service

import javax.inject.Inject

import model._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository.{AlbumGenreRepository, AlbumRepository, ArtistRepository, GenreRepository}
import slick.dbio.DBIO
import table.Tables.dbConfig._
import table.Tables.dbConfig.driver.api._

import scala.concurrent.Future

class AlbumService @Inject()(private val albumRepository: AlbumRepository,
                             private val artistRepository: ArtistRepository,
                             private val albumGenreRepository: AlbumGenreRepository,
                             private val genreRepository: GenreRepository) {

  def save(albumVal: AlbumWithArtistAndGenres): Future[AlbumWithArtistAndGenres] = {
    val query = albumRepository.existsAlbumWithArtist(albumVal.name, albumVal.artist).flatMap {
      case true => DBIO.failed(new Exception("Album with such name and artist name already exists"))
      case false => artistRepository.findByName(albumVal.artist).flatMap {
        case None => DBIO.failed(new Exception(s"Artist with name: '${albumVal.artist}' doesn't exist"))
        case Some(artist) => genreRepository.saveNotExistedGenres(albumVal.genres).flatMap { genres =>
          albumRepository.save(Album(albumVal.name, albumVal.year, artist.id.get), genres).flatMap { album =>
            albumGenreRepository.save(album, genres).map { count =>
              AlbumWithArtistAndGenres(album.id, album.name, album.year, artist.name, genres.map(_.name))
            }
          }
        }
      }
    }

    db.run(query.transactionally)
  }

  def delete(id: Int): Future[Int] = db.run {
    albumRepository.delete(id)
  }

  def findAll(from: Int, limit: Int): Future[Seq[AlbumWithArtistAndGenres]] = {
    findGenresFor(
      albumRepository.findAllAlbumsWithArtist(from, limit)
    )
  }

  private def findGenresFor(albumQuery: DBIO[Seq[(Album, String)]]): Future[Seq[AlbumWithArtistAndGenres]] = {
    val query =
      albumQuery.flatMap { albumsWithArtist =>
        DBIO.sequence(
          albumsWithArtist.map { case (album, artist) => findGenresFor(album, artist) }
        )
      }

    db.run(query)
  }

  private def findGenresFor(album: Album, artist: String): DBIO[AlbumWithArtistAndGenres] = {
    genreRepository.findByAlbumId(album.id.get).map { genres =>
      AlbumWithArtistAndGenres(album.id, album.name, album.year, artist, genres.map(_.name))
    }
  }

  def findById(id: Int): Future[Option[AlbumWithArtistAndGenres]] = {
    findGenresFor(
      albumRepository.findByIdAlbumWithArtist(id).map(_.toSeq)
    ) map (_.headOption)
  }
}
