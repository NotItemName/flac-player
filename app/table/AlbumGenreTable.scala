package table


import table.Tables.dbConfig.driver.api._
import table.Tables._

class AlbumGenreTable(tag: Tag) extends Table[(Int, Int)](tag,"ALBUM_GENRE"){
  override def * = (albumId, genreId)

  def albumId= column[Int]("albumId")
  def genreId= column[Int]("genreId")

  def album = foreignKey("ALBUM_FK", albumId, albumTable)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def genre = foreignKey("GENRE_FK", genreId, genreTable)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

}
