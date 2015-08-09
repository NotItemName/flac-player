package controllers

import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import service.AlbumService

import scala.concurrent.Future


class AlbumController @Inject()(private val albumService: AlbumService) extends Controller{

  def getAll(from: Int, limit: Int) = Action.async {
    if (limit <= 100) {
      albumService.findAll(from, limit).map { albums =>
        Ok(Json.obj("albums" -> albums))
      }
    } else {
      Future.successful(BadRequest("Limit number must be <= 100"))
    }
  }

  def getById(id: Int) = Action.async {
    albumService.findById(id).map {
      case Some(album) => Ok(Json.toJson(album))
      case None => NotFound("Album Not Found")
    }
  }

}
