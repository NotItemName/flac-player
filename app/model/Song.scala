package model

import play.api.libs.json.Json


case class Song(name: String, trackNumber: Int, fileName: String,
                artistId: Int, albumId: Int, id: Option[Int]) extends Model[Song] {
  override def withId(id: Int): Song = this.copy(id = Some(id))
}

object Song{
  implicit val jsonFormat = Json.format[Song]
}