package service

import com.google.inject.Inject
import model.Artist
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository.ArtistRepository
import table.Tables.dbConfig._
import table.Tables.dbConfig.driver.api._

import scala.concurrent.Future

/**
 * @author Mykola Zalyayev
 */
class ArtistService @Inject()(private val artistRepository: ArtistRepository) {

  def delete(id: Int): Future[Int] = db.run {
    artistRepository.delete(id)
  }

  def update(id: Int, artist: Artist): Future[Int] = db.run {
    artistRepository.update(id, artist)
  }

  def save(artist: Artist): Future[Artist] = db.run {
    artistRepository.findByName(artist.name).flatMap {
      case None => artistRepository.save(artist)
      case Some(foundedArtist) => DBIO.successful(foundedArtist)
    }.transactionally
  }

  def findAll(from: Int, limit: Int): Future[Seq[Artist]] = db.run {
    artistRepository.findAll(from, limit)
  }

  def findById(id: Int): Future[Option[Artist]] = db.run {
    artistRepository.findById(id)
  }
}
