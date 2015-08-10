package repository

import model.Song
import table.SongTable
import table.Tables._


class SongRepository extends GenericRepository[Song, SongTable](songTable){

  override def copyWithId(song: Song, id: Int): Song = song.copy(id = Some(id))
}
