package ntdalbec.sequencinator

import android.app.Application
import androidx.room.Room
import java.util.*

class DatabaseManager private constructor(appContext: Application) {
    companion object {
        private var INSTANCE: DatabaseManager? = null

        fun getInstance(appContext: Application): DatabaseManager {
            if (INSTANCE == null) {
                INSTANCE = DatabaseManager(appContext)
            }
            return INSTANCE!!
        }
    }

    private val db = Room.databaseBuilder(
        appContext,
        SongDatabase::class.java,
        "song_db"
    ).build()

    val songs: Array<Song>
        get() = db.songDao().loadAllSongs()

    fun addSong(name: String): Song {
        val uuid = UUID.randomUUID()
        val song = Song(uuid, name)
        db.songDao().insertSongs(song)
        return song
    }

    fun getChannelsBySong(id: UUID): List<Channel> {
        val chanEntities = db.channelDao().loadChannelsBySong(id)
        return chanEntities.map(fun(it: ChannelEntity): Channel {
            val notes = mutableListOf<Note>()
            it.noteData?.let {
                for (i in 0 until it.length step 2) {
                    val tone = it[i].toInt()
                    val duration = it[i + 1].toInt()
                    val note = Note(tone, duration)
                    notes.add(note)
                }
            }
            val wave = WaveForms.Wave.valueOf(it.waveName)
            return Channel(wave, notes, it.uid)
        })
    }
}