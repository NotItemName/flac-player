package service

import javax.inject.Inject

import model._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository.{AlbumGenreRepository, AlbumRepository, ArtistRepository, GenreRepository}
import table.Tables.dbConfig._
import table.Tables.dbConfig.driver.api._
import view.AlbumWithArtistAndGenres

import scala.concurrent.Future

class AlbumService @Inject()(private val albumRepository: AlbumRepository,
                             private val artistRepository: ArtistRepository,
                             private val albumGenreRepository: AlbumGenreRepository,
                             private val genreRepository: GenreRepository) {

  def save(albumVal: AlbumWithArtistAndGenres): Future[(Album, String, Seq[String])] = {
    val query = artistRepository.findByName(albumVal.artist).flatMap {
      case None => DBIO.failed(new Exception(s"Artist with name: '${albumVal.artist}' doesn't exist"))
      case Some(artist) => genreRepository.saveNotExistedGenres(albumVal.genres).flatMap { genres =>
        albumRepository.save(Album(albumVal.name, albumVal.year, artist.id.get), genres).flatMap { album =>
          albumGenreRepository.save(album, genres).map(_ => (album, artist.name, genres.map(_.name)))
        }
      }
    }

    db.run(query.transactionally)
  }

  def delete(id: Int): Future[Int] = db.run {
    albumRepository.delete(id)
  }

  def findAll(from: Int, limit: Int): Future[Seq[(Album, String, Seq[String])]] = {
    findGenresFor(
      albumRepository.findAllAlbumsWithArtist(from, limit)
    )
  }

  def findById(id: Int): Future[Option[(Album, String, Seq[String])]] = {
    findGenresFor(
      albumRepository.findByIdAlbumWithArtist(id).map(_.toSeq)
    ) map (_.headOption)
  }

  private def findGenresFor(albumQuery: DBIO[Seq[(Album, String)]]): Future[Seq[(Album, String, Seq[String])]] = {
    val query =
      albumQuery.flatMap {
        albumsWithArtist =>
          DBIO.sequence(
            albumsWithArtist.map {
              case (album, artist) => findGenresFor(album, artist)
            }
          )
      }

    db.run(query)
  }

  private def findGenresFor(album: Album, artist: String): DBIO[(Album, String, Seq[String])] = {
    genreRepository.findByAlbumId(album.id.get).map {
      genres => (album, artist, genres.map(_.name))
    }
  }
}
