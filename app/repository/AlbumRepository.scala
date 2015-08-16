package repository

import model.{Album, Artist, Genre}
import table.AlbumTable
import table.Tables._
import table.Tables.dbConfig.driver.api._

class AlbumRepository extends GenericRepository[Album, AlbumTable](albumTable) {

  def save(album: Album, genres: Seq[Genre]): DBIO[Album] = {
    (albumTable returning albumTable.map(_.id)
      into ((newAlbum, id) => newAlbum.copy(id = Some(id)))
      ) += album
  }

  def findByIdAlbumWithArtist(id: Int): DBIO[Seq[(Album, Artist, Genre)]] = {
    fetchData(albumTable.filter(_.id === id))
  }

  def findAllAlbumsWithArtistAndGenres(from: Int, limit: Int): DBIO[Seq[(Album, Artist, Genre)]] = {
    fetchData(albumTable.drop(from).take(limit))
  }

  private def fetchData(albumQuery: Query[AlbumTable, AlbumTable#TableElementType, Seq]): DBIO[Seq[(Album, Artist, Genre)]] = {
    (for {
      album <- albumQuery
      artist <- album.artist
      albumGenre <- albumGenreTable if album.id === albumGenre.albumId
      genre <- genreTable if albumGenre.genreId === genre.id
    } yield (album, artist, genre)).result
  }

}
