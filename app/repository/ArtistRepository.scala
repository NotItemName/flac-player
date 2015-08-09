package repository

import model.Artist
import table.Tables.dbConfig.driver.api._
import table.Tables.artistTable

/**
 * @author Mykola Zalyayev
 */
class ArtistRepository {

  def delete(id: Int): DBIO[Int] = artistTable.filter(_.id === id).delete

  def update(id: Int, artist: Artist): DBIO[Int] = artistTable.filter(_.id === id).map(_.name).update(artist.name)

  def findByName(name: String): DBIO[Option[Artist]] = artistTable.filter(_.name === name).result.headOption

  def findAll(from: Int, limit: Int): DBIO[Seq[Artist]] = artistTable.drop(from).take(limit).result

  def findById(id: Int): DBIO[Option[Artist]] = artistTable.filter(_.id === id).result.headOption.transactionally

  def save(artist: Artist): DBIO[Artist] = {
    (artistTable returning artistTable.map(_.id)
      into ((newArtist, id) => newArtist.copy(id = Some(id)))
      ) += artist
  }


}
