package model

import play.api.libs.json.Json

case class Artist(name: String, id: Option[Int] = None) extends Model[Artist] {
  override def withId(id: Int): Artist = this.copy(id = Some(id))
}

object Artist {
  implicit val jsonFormat = Json.format[Artist]
}
