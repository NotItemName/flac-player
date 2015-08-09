package repository

import model.Album
import table.Tables._
import table.Tables.dbConfig.driver.api._

class AlbumRepository {
  def findById(id: Int): DBIO[Option[(Album, String)]] = {
    (for {
      album <- albumTable
      artist <- album.artist if album.id === id
    } yield (album, artist.name)).result.headOption
  }


  def findAll(from: Int, limit: Int): DBIO[Seq[(Album, String)]] = {
    (for {
      album <- albumTable
      artist <- album.artist
    } yield (album, artist.name)).result
  }

}
