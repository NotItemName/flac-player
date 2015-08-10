package model

import play.api.libs.json.Json


case class Genre(name: String, id: Option[Int] = None) extends Model[Genre] {
  override def withId(id: Int): Genre = this.copy(id = Some(id))
}

object Genre {
  implicit val jsonFormat = Json.format[Genre]
}