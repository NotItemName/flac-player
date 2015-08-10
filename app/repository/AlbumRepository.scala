package repository

import model.{Album, Genre}
import table.AlbumTable
import table.Tables._
import table.Tables.dbConfig.driver.api._

class AlbumRepository extends GenericRepository[Album, AlbumTable](albumTable) {

  def save(album: Album, genres: Seq[Genre]): DBIO[Album] = {
    (albumTable returning albumTable.map(_.id)
      into ((newAlbum, id) => newAlbum.copy(id = Some(id)))
      ) += album
  }

  def existsAlbumWithArtist(albumName: String, artistName: String): DBIO[Boolean] = {
    (for {
      album <- albumTable if album.name === albumName
      artist <- album.artist if artist.name === artistName
    } yield album).exists.result
  }


  def findByIdAlbumWithArtist(id: Int): DBIO[Option[(Album, String)]] = {
    (for {
      album <- albumTable
      artist <- album.artist if album.id === id
    } yield (album, artist.name)).result.headOption
  }


  def findAllAlbumsWithArtist(from: Int, limit: Int): DBIO[Seq[(Album, String)]] = {
    (for {
      album <- albumTable
      artist <- album.artist
    } yield (album, artist.name)).result
  }

}
