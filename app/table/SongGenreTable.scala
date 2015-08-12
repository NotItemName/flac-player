package table

import table.Tables._
import table.Tables.dbConfig.driver.api._

class SongGenreTable(tag: Tag) extends Table[(Int, Int)](tag,"SONG_GENRE"){
  override def * = (songId, genreId)

  def songId= column[Int]("songId")
  def genreId= column[Int]("genreId")

  def song = foreignKey("SONG_FK", songId, songTable)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def genre = foreignKey("GENRE_FK", genreId, genreTable)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

}
