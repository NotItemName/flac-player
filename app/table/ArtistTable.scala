package table

import model.Artist
import table.Tables.dbConfig.driver.api._

class ArtistTable(tag: Tag) extends Table[Artist](tag, "ARTIST") {
  override def * = (id.?, name) <>((Artist.apply _).tupled, Artist.unapply)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

}