package table


import model.Song
import table.Tables._
import table.Tables.dbConfig.driver.api._

class SongTable(tag: Tag) extends BaseTable[Song](tag, "SONG") {
  override def * = (name, trackNumber, fileName, albumId, artistId, id.?) <>((Song.apply _).tupled, Song.unapply)

  override def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def trackNumber = column[String]("trackNumber")
  def artistId = column[Int]("artistId")
  def fileName = column[String]("fileName")
  def albumId = column[Int]("albumId")

  def uniqueFileName = index("IDX_FILENAME", fileName, unique = true)
  def artist = foreignKey("artist_song_fk", artistId, artistTable)(_.id)
  def album = foreignKey("album_song_fk", albumId, albumTable)(_.id)

}
