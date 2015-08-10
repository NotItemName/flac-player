package model

import play.api.libs.json.Json

case class Artist(id:Option[Int], name:String) extends Model[Artist] {
  override def withId(id: Int): Artist = this.copy(id = Some(id))
}

object Artist {
  implicit val jsonFormat = Json.format[Artist]
}
