package service

import com.google.inject.Inject
import model.Genre
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository.GenreRepository
import slick.driver.JdbcProfile
import table.Tables

import scala.concurrent.Future

import Tables.dbConfig._
import Tables.dbConfig.driver.api._
class GenreService @Inject()(private val genreRepository: GenreRepository) {

  def update(id: Int, genre: Genre): Future[Int] = db.run {
    genreRepository.update(id, genre)
  }

  def delete(id: Int): Future[Int] = db.run {
    genreRepository.delete(id)
  }

  def save(genre: Genre): Future[Genre] = db.run {
    genreRepository.findByName(genre.name).flatMap {
      case None => genreRepository.save(genre)
      case Some(foundGenre) => DBIO.failed(new Exception("Genre already exists"))
    }.transactionally
  }

  def findAll(from: Int, limit: Int): Future[Seq[Genre]] = db.run {
    genreRepository.findAll(from, limit)
  }

  def findById(id: Int): Future[Option[Genre]] = db.run {
    genreRepository.findById(id)
  }

}
