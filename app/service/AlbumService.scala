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
    findGenresFor(
      albumRepository.findAllAlbumsWithArtist(from, limit)
    )
  }

  def findById(id: Int): Future[Option[AlbumWithArtistAndGenres]] = {
    findGenresFor(
      albumRepository.findByIdAlbumWithArtist(id).map(_.toSeq)
    ) map (_.headOption)
  }

  private def findGenresFor(albumQuery: DBIO[Seq[(Album, Artist)]]): Future[Seq[AlbumWithArtistAndGenres]] = {
    val query =
      albumQuery.flatMap { albumsWithArtist =>
        DBIO.sequence(
          albumsWithArtist.map { case (album, artist) => findGenresFor(album, artist) }
        )
      }

    db.run(query)
  }

  private def findGenresFor(album: Album, artist: Artist): DBIO[AlbumWithArtistAndGenres] = {
    genreRepository.findByAlbumId(album.id.get).map { genres =>
      AlbumWithArtistAndGenres(album.id, album.name, album.year, artist.name, genres.map(_.name))
    }
  }

}
