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
    groupBy(albumRepository.findAllAlbumsWithArtistAndGenres(from, limit))
  }

  def findById(id: Int): Future[Option[(Album, String, Seq[String])]] = {
    groupBy(albumRepository.findByIdAlbumWithArtist(id)).map(_.headOption)
  }

  private def groupBy(query: DBIO[Seq[(Album, Artist, Genre)]]): Future[Seq[(Album, String, Seq[String])]] = {
    db.run(query).map { result =>
      result.groupBy(y => (y._1, y._2)).map {
        case (((album, artist), tail)) => (album, artist.name, tail.map(_._3.name))
      }.toSeq
    }
  }
}
