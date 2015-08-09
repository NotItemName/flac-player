package repository

import model.Genre
import table.Tables._
import table.Tables.dbConfig.driver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class GenreRepository {

  def saveNotExistedGenres(genres: Seq[String]): DBIO[Seq[Genre]] = {
    DBIO.sequence(genres.map { genreName =>
      findByName(genreName).flatMap {
        case None => save(Genre(genreName))
        case Some(foundGenre) => DBIO.successful(foundGenre)
      }
    })
  }

  def findByName(name: String): DBIO[Option[Genre]] = genreTable.filter(_.name === name).result.headOption

  def save(genre: Genre): DBIO[Genre] = {
    (genreTable returning genreTable.map(_.id)
      into ((newGenre, id) => newGenre.copy(id = Some(id)))
      ) += genre
  }

  def findAll(from: Int, limit: Int): DBIO[Seq[Genre]] = genreTable.drop(from).take(limit).result

  def findByAlbumId(id: Int): DBIO[Seq[Genre]] = {
    val query = for {
      genre <- genreTable
      albGenreTable <- albumGenreTable if albGenreTable.albumId === id && genre.id === albGenreTable.genreId
    } yield genre
    query.result
  }

  def delete(id: Int): DBIO[Int] = genreTable.filter(_.id === id).delete

  def update(id: Int, genre: Genre): DBIO[Int] = genreTable.filter(_.id === id).map(_.name).update(genre.name)

  def findById(id: Int): DBIO[Option[Genre]] = genreTable.filter(_.id === id).result.headOption.transactionally

}
