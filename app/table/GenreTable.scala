package table


import model.Genre
import table.Tables.dbConfig.driver.api._

class GenreTable(tag: Tag) extends Table[Genre](tag, "GENRE") {
  override def * = (name, id.?) <>((Genre.apply _).tupled, Genre.unapply)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

}
