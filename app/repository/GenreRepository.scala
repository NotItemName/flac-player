package repository

import javax.inject.Inject

import model.Genre
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile


class GenreRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val genres = TableQuery[GenreTable]

  def findByName(name: String): DBIO[Option[Genre]] = genres.filter(_.name === name).result.headOption

  def findAll(from: Int, limit: Int): DBIO[Seq[Genre]] = genres.drop(from).take(limit).result

  def delete(id: Int): DBIO[Int] = genres.filter(_.id === id).delete

  def save(genre: Genre): DBIO[Genre] = {
    (genres returning genres.map(_.id)
      into ((newGenre, id) => newGenre.copy(id = Some(id)))
      ) += genre
  }

  def update(id: Int, genre: Genre): DBIO[Int] = genres.filter(_.id === id).map(_.name).update(genre.name)

  def findById(id: Int): DBIO[Option[Genre]] = genres.filter(_.id === id).result.headOption.transactionally


  class GenreTable(tag: Tag) extends Table[Genre](tag, "GENRE") {
    override def * = (id.?, name) <>((Genre.apply _).tupled, Genre.unapply)

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")
  }

}
