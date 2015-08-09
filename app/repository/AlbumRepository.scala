package repository

import model.{Artist, Album}
import table.Tables._
import table.Tables.dbConfig.driver.api._

class AlbumRepository {
  def findByIdAlbumWithArtist(id: Int): DBIO[Option[(Album, Artist)]] = {
    (for {
      album <- albumTable
      artist <- album.artist if album.id === id
    } yield (album, artist)).result.headOption
  }


  def findAllAlbumsWithArtist(from: Int, limit: Int): DBIO[Seq[(Album, Artist)]] = {
    (for {
      album <- albumTable
      artist <- album.artist
    } yield (album, artist)).result
  }

}
