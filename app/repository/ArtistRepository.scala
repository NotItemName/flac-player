package repository

import model.Artist
import table.ArtistTable
import table.Tables.artistTable
import table.Tables.dbConfig.driver.api._


class ArtistRepository extends GenericRepository[Artist, ArtistTable](artistTable) {

  def findByName(name: String): DBIO[Option[Artist]] = artistTable.filter(_.name === name).result.headOption

}
