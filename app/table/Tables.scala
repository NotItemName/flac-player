package table

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile


object Tables {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("default")(Play.current)
  import dbConfig.driver.api._

  lazy val albumTable = TableQuery[AlbumTable]
  lazy val genreTable = TableQuery[GenreTable]
  lazy val songTable = TableQuery[SongTable]
  lazy val artistTable = TableQuery[ArtistTable]
  lazy val albumGenreTable = TableQuery[AlbumGenreTable]
  lazy val songGenreTable = TableQuery[SongGenreTable]

}






