package service

import com.google.inject.Inject
import model.Genre
import repository.GenreRepository
import table.Tables.dbConfig._

import scala.concurrent.Future

class GenreService @Inject()(private val genreRepository: GenreRepository) {

  def update(id: Int, genre: Genre): Future[Int] = db.run {
    genreRepository.update(id, genre)
  }

  def delete(id: Int): Future[Int] = db.run {
    genreRepository.delete(id)
  }

  def save(genre: Genre): Future[Genre] = db.run {
    genreRepository.save(genre)
  }

  def findAll(from: Int, limit: Int): Future[Seq[Genre]] = db.run {
    genreRepository.findAll(from, limit)
  }

  def findById(id: Int): Future[Option[Genre]] = db.run {
    genreRepository.findById(id)
  }

}
