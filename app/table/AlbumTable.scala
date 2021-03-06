package table

import model.Album
import table.Tables._
import table.Tables.dbConfig.driver.api._


class AlbumTable(tag: Tag) extends BaseTable[Album](tag, "ALBUM") {
  override def * = (name, year, artistId, id.?) <>((Album.apply _).tupled, Album.unapply)

  override def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def year = column[Int]("year")
  def artistId = column[Int]("artistId")

  def artist = foreignKey("artist_album_fk", artistId, artistTable)(_.id)
  def uniqueAlbumArtist = index("album_artist_idx", (name, artistId), unique = true)
}
