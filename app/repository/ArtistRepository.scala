package repository

import model.Artist
import table.ArtistTable
import table.Tables.dbConfig.driver.api._
import table.Tables.artistTable


class ArtistRepository extends GenericRepository[Artist, ArtistTable](artistTable){

  def findByName(name: String): DBIO[Option[Artist]] = artistTable.filter(_.name === name).result.headOption

  def save(artist: Artist): DBIO[Artist] = {
    (artistTable returning artistTable.map(_.id)
      into ((newArtist, id) => newArtist.copy(id = Some(id)))
      ) += artist
  }


}
