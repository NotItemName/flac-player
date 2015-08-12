package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import service.SongService
import view.SongWithAlbumArtistAndGenres

class SongController @Inject()(private val songService: SongService) extends Controller {

  def getAll(from: Int, to: Int) = Action.async {
    songService.findAll(from, to).map { songs =>
      Ok(Json.obj("songs" -> songs.map(SongWithAlbumArtistAndGenres.transform(_))))
    }
  }

  def getById(id: Int) = Action.async {
    songService.findById(id).map {
      case Some(song) => Ok(Json.toJson(SongWithAlbumArtistAndGenres.transform(song)))
      case None => NotFound("Song Not Found")
    }
  }

  def delete(id: Int) = Action.async {
    songService.delete(id).map {
      case 0 => NotFound("Song Not Found")
      case _ => NoContent
    }
  }

}
