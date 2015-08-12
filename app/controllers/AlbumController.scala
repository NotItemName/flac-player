package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import service.AlbumService
import view.AlbumWithArtistAndGenres

import scala.concurrent.Future
import scala.util.control.NonFatal


class AlbumController @Inject()(private val albumService: AlbumService) extends Controller {

  def getAll(from: Int, limit: Int) = Action.async {
    if (limit <= 100) {
      albumService.findAll(from, limit).map { albums =>
        Ok(Json.obj("albums" -> albums.map(AlbumWithArtistAndGenres.transform)))
      }
    } else {
      Future.successful(BadRequest("Limit number must be <= 100"))
    }
  }

  def getById(id: Int) = Action.async {
    albumService.findById(id).map {
      case Some(album) => Ok(Json.toJson(AlbumWithArtistAndGenres.transform(album)))
      case None => NotFound("Album Not Found")
    }
  }

  def add() = Action.async(parse.json) { implicit request =>
    request.body.validate[AlbumWithArtistAndGenres].map { album =>
      albumService.save(album).map { savedAlbum =>
        Ok(Json.toJson(AlbumWithArtistAndGenres.transform(savedAlbum)))
      }.recover {
        case NonFatal(ex) => BadRequest(ex.getMessage)
      }
    }.getOrElse(Future.successful(BadRequest("Invalid json \n" + request.body)))
  }

  def delete(id: Int) = Action.async {
    albumService.delete(id).map {
      case 0 => NotFound("Album Not Found")
      case _ => NoContent
    }
  }


}
