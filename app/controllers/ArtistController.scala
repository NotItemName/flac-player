package controllers

import com.google.inject.Inject
import model.Artist
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import service.ArtistService

import scala.concurrent.Future

/**
 * @author Mykola Zalyayev
 */
class ArtistController @Inject()(val artistService: ArtistService) extends Controller {

  def getAll(from: Int, limit: Int) = Action.async {
    if (limit <= 100) {
      artistService.findAll(from, limit).map { artists =>
        Ok(Json.obj("artists" -> artists))
      }
    } else {
      Future.successful(BadRequest("Limit number must be <= 100"))
    }
  }

  def getById(id: Int) = Action.async {
    artistService.findById(id).map {
      case Some(genre) => Ok(Json.toJson(genre))
      case None => NotFound("Artist Not Found")
    }
  }

  def add() = Action.async(parse.json) { implicit request =>
    request.body.validate[Artist].map { artist =>
      artistService.save(artist).map { savedArtist =>
        Ok(Json.toJson(savedArtist))
      }
    }.getOrElse(Future.successful(BadRequest("Invalid json")))
  }

  def update(id: Int) = Action.async(parse.json) { implicit request =>
    request.body.validate[Artist].map { artist =>
      artistService.update(id, artist).map {
        case 0 => NotFound("Artist Not Found")
        case _ => NoContent
      }
    }.getOrElse(Future.successful(BadRequest("Invalid json")))
  }

  def delete(id:Int) = Action.async {
    artistService.delete(id).map {
      case 0 => NotFound("Artist Not Found")
      case _ => NoContent
    }
  }

}
