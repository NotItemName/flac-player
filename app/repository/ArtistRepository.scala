package repository

import javax.inject.Inject

import model.Artist
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
 * @author Mykola Zalyayev
 */
class ArtistRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {


  import driver.api._

  val artists = TableQuery[ArtistTable]

  def delete(id: Int): DBIO[Int] = artists.filter(_.id === id).delete

  def update(id: Int, artist: Artist): DBIO[Int] = artists.filter(_.id === id).map(_.name).update(artist.name)

  def findByName(name: String): DBIO[Option[Artist]] = artists.filter(_.name === name).result.headOption

  def findAll(from: Int, limit: Int): DBIO[Seq[Artist]] = artists.drop(from).take(limit).result

  def findById(id: Int): DBIO[Option[Artist]] = artists.filter(_.id === id).result.headOption.transactionally

  def save(artist: Artist): DBIO[Artist] = {
    (artists returning artists.map(_.id)
      into ((newArtist, id) => newArtist.copy(id = Some(id)))
      ) += artist
  }

  class ArtistTable(tag: Tag) extends Table[Artist](tag, "ARTIST") {
    override def * = (id.?, name) <>((Artist.apply _).tupled, Artist.unapply)

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")
  }

}
