package controllers

import javax.inject.Inject

import model.Genre
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import service.GenreService

import scala.concurrent.Future
import scala.util.control.NonFatal

class GenreController @Inject()(val genreService: GenreService) extends Controller {

  def getAll(from: Int, limit: Int) = Action.async {
    if (limit <= 100) {
      genreService.findAll(from, limit).map { genres =>
        Ok(Json.obj("generes" -> genres))
      }
    } else {
      Future.successful(BadRequest("Limit number must be <= 100"))
    }
  }

  def getById(id: Int) = Action.async {
    genreService.findById(id).map {
      case Some(genre) => Ok(Json.toJson(genre))
      case None => NotFound("Genre Not Found")
    }
  }

  def add() = Action.async(parse.json) { implicit request =>
    request.body.validate[Genre].map { genre =>
      genreService.save(genre).map { savedGenre =>
        Ok(Json.toJson(savedGenre))
      }.recover {
        case NonFatal(ex) => BadRequest(ex.getMessage)
      }
    }.getOrElse(Future.successful(BadRequest("Invalid json")))
  }

  def update(id: Int) = Action.async(parse.json) { implicit request =>
    request.body.validate[Genre].map { genre =>
      genreService.update(id, genre).map {
        case 0 => NotFound("Genre Not Found")
        case _ => NoContent
      }
    }.getOrElse(Future.successful(BadRequest("Invalid json")))
  }

  def delete(id: Int) = Action.async {
    genreService.delete(id).map {
      case 0 => NotFound("Genre Not Found")
      case _ => NoContent
    }
  }
}
