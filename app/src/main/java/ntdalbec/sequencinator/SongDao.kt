package ntdalbec.sequencinator

import androidx.room.*
import java.util.*

@Dao
interface SongDao {
    @Insert
    fun insertSongs(vararg songs: Song)

    @Update
    fun updateSongs(vararg songs: Song)

    @Delete
    fun deleteSongs(vararg songs: Song)

    @Query("SELECT * FROM song")
    fun loadAllSongs(): Array<Song>

    @Query("SELECT * FROM song WHERE uid = :id LIMIT 1")
    fun loadSongById(id: UUID): Song
}