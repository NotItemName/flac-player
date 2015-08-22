package repository

import model.{Album, Artist, Genre, Song}
import table.SongTable
import table.Tables._
import table.Tables.dbConfig.driver.api._


class SongRepository extends GenericRepository[Song, SongTable](songTable) {

  def findFileNameById(id: Int): DBIO[Option[String]] = songTable.filter(_.id === id).map(_.fileName).result.headOption

  def findByIdSongsWithArtistAndAlbum(id: Int):DBIO[Option[(Song, Album, Artist)]] = {
    (for {
      song <- songTable if song.id === id
      album <- song.album
      artist <- song.artist
    } yield (song, album, artist)).result.headOption
  }


  def findAllSongsWithArtistAndAlbum(from: Int, to: Int): DBIO[Seq[(Song, Album, Artist)]] = {
    (for {
      song <- songTable
      album <- song.album
      artist <- song.artist
    } yield (song, album, artist)).result
  }
}
