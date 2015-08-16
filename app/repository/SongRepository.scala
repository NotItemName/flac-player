package repository

import model.{Album, Artist, Genre, Song}
import table.SongTable
import table.Tables._
import table.Tables.dbConfig.driver.api._


class SongRepository extends GenericRepository[Song, SongTable](songTable) {

  def findByIdSongsWithArtistAlbumAndGenres(id: Int): DBIO[Seq[(Song, Album, Artist, Genre)]] = {
    fetchData(songTable.filter(_.id === id))
  }

  def findAllSongsWithArtistAlbumAndGenre(from: Int, limit: Int): DBIO[Seq[(Song, Album, Artist, Genre)]] = {
    fetchData(songTable.drop(from).take(limit))
  }

  private def fetchData(songQuery: Query[SongTable, SongTable#TableElementType, Seq]): DBIO[Seq[(Song, Album, Artist, Genre)]] = {
    (for {
      song <- songQuery
      album <- song.album
      artist <- song.artist
      songGenre <- songGenreTable if song.id === songGenre.songId
      genre <- genreTable if songGenre.genreId === genre.id
    } yield (song, album, artist, genre)).result
  }

}
