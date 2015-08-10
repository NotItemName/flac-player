package repository

import model.Artist
import table.ArtistTable
import table.Tables.dbConfig.driver.api._
import table.Tables.artistTable


class ArtistRepository extends GenericRepository[Artist, ArtistTable](artistTable){

  override def copyWithId(artist: Artist, id: Int): Artist = artist.copy(id = Some(id))

  def findByName(name: String): DBIO[Option[Artist]] = artistTable.filter(_.name === name).result.headOption

}
