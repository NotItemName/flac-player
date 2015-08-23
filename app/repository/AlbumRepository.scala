package repository

import model.{Album, Artist, Genre}
import slick.dbio
import table.AlbumTable
import table.Tables._
import table.Tables.dbConfig.driver.api._

class AlbumRepository extends GenericRepository[Album, AlbumTable](albumTable) {

  def findByName(name: String): DBIO[Option[Album]] = albumTable.filter(_.name === name).result.headOption

  def findByIdAlbumWithArtist(id: Int): DBIO[Option[(Album, String)]] = {
    (for {
      album <- albumTable if album.id === id
      artist <- album.artist
    } yield (album, artist.name)).result.headOption
  }


  def findAllAlbumsWithArtist(from: Int, limit: Int): DBIO[Seq[(Album, String)]] = {
    (for {
      album <- albumTable
      artist <- album.artist
    } yield (album, artist.name)).result
  }

}
