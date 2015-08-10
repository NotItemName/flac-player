package repository

import model.Genre
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import table.GenreTable
import table.Tables._
import table.Tables.dbConfig.driver.api._

class GenreRepository extends GenericRepository[Genre, GenreTable](genreTable) {

  override def copyWithId(genre: Genre, id: Int): Genre = genre.copy(id = Some(id))

  def saveNotExistedGenres(genres: Seq[String]): DBIO[Seq[Genre]] = {
    DBIO.sequence(genres.map { genreName =>
      findByName(genreName).flatMap {
        case None => save(Genre(genreName))
        case Some(foundGenre) => DBIO.successful(foundGenre)
      }
    })
  }

  def findByName(name: String): DBIO[Option[Genre]] = genreTable.filter(_.name === name).result.headOption

  def findByAlbumId(id: Int): DBIO[Seq[Genre]] = {
    val query = for {
      genre <- genreTable
      albGenreTable <- albumGenreTable if albGenreTable.albumId === id && genre.id === albGenreTable.genreId
    } yield genre
    query.result
  }

}
