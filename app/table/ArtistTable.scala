package table

import model.Artist
import table.Tables.dbConfig.driver.api._

class ArtistTable(tag: Tag) extends BaseTable[Artist](tag, "ARTIST") {
  override def * = (name, id.?) <>((Artist.apply _).tupled, Artist.unapply)

  override def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def uniqueName = index("artist_uniqueName", name, unique = true)
}