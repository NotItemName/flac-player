package controllers

import java.io._
import java.util.UUID
import javax.inject.Inject

import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.ParseContext
import org.gagravarr.tika.FlacParser
import org.xml.sax.helpers.DefaultHandler
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import service.SongService
import util.ImplicitsUtils.TryOps
import view.SongWithAlbumArtistAndGenres

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

class SongController @Inject()(private val songService: SongService,
                               private val configuration: Configuration) extends Controller {

  private val pathToMusicFolder = configuration.getString("music.folder").get

  def getAll(from: Int, to: Int) = Action.async {
    songService.findAll(from, to).map { songs =>
      Ok(Json.obj("songs" -> songs.map(SongWithAlbumArtistAndGenres.transform)))
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

  def downloadSong(id: Int) = Action.async {
    songService.findFileNameById(id).map {
      case None => NotFound("Song Not found")
      case Some(fileName) =>
        val file = new File(pathToMusicFolder + fileName)
        if (file.exists()) Ok.sendFile(file, fileName = _.getName)
        else NotFound("Song file not found")
    }
  }

  def saveSong = Action.async(parse.multipartFormData) { request =>
    request.body.file("song").fold(Future.successful(NotFound("Missing file"))) { songFile =>
      import java.io.File
      val file = new File(pathToMusicFolder + UUID.randomUUID().toString + ".flac")
      getMetadata(songFile.ref.file).map(metadata => convertFromMetadata(metadata)) match {
        case Success(song) =>
          songService.save(song, file.getName).map { song =>
            songFile.ref.moveTo(file)
            Ok(Json.toJson(song))
          }
        case Failure(_) => Future.successful(BadRequest("Couldn't parse metadata"))
      }
    }.recover {
      case NonFatal(e) => BadRequest(e.getMessage)
    }
  }

  private def convertFromMetadata(metadata: Metadata): SongWithAlbumArtistAndGenres = {
    val title = metadata.get("title")
    val trackNumber = metadata.get("xmpDM:trackNumber").toInt
    val genres = metadata.get("xmpDM:genre").split(",").toSeq
    val album = metadata.get("xmpDM:album")
    val year = metadata.get("xmpDM:releaseDate").toInt
    val artist = metadata.get("xmpDM:artist")

    SongWithAlbumArtistAndGenres(None, title, year, trackNumber, artist, album, genres)
  }

  private def getMetadata(file: File): Try[Metadata] = {
    val input = new BufferedInputStream(new FileInputStream(file))
    val handler = new DefaultHandler()
    val metadata = new Metadata()
    val parser = new FlacParser()
    val parseCtx = new ParseContext()
    Try {
      parser.parse(input, handler, metadata, parseCtx)
      metadata
    }.eventually(input.close())
  }


}

