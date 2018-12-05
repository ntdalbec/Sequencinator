package ntdalbec.sequencinator

import android.app.Application
import androidx.room.Room
import java.util.*

class DatabaseManager private constructor(appContext: Application) : SequencerStore {
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
        SequenceDatabase::class.java,
        "song_db"
    ).build()

    override val songs: Array<Song>
        get() = db.songDao().loadAllSongs()

    override fun addSong(name: String): Song {
        val uuid = UUID.randomUUID()
        val song = Song(uuid.toString(), name)
        db.songDao().insertSongs(song)
        return song
    }

    override fun deleteSong(song: Song) {
        db.songDao().deleteSongs(song)
    }

    override fun updateSong(song: Song) {
        db.songDao().updateSongs(song)
    }

    override fun getChannelsBySong(id: UUID): List<Channel> {
        val chanEntities = db.channelDao().loadChannelsBySong(id.toString())
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
            val id = UUID.fromString(it.uid)
            return Channel(wave, id, notes)
        })
    }

    override fun addChannel(channel: Channel, songId: UUID) {
        val entity = channelToEntity(channel, songId)
        db.channelDao().insertChannels(entity)
    }

    override fun updateChannel(channel: Channel, songId: UUID) {
        val entity = channelToEntity(channel, songId)
        db.channelDao().updateChannels(entity)
    }

    override fun deleteChannel(channel: Channel, songId: UUID) {
        val entity = channelToEntity(channel, songId)
        db.channelDao().deleteChannels(entity)
    }

    private fun channelToEntity(channel: Channel, songId: UUID): ChannelEntity {
        val waveName = channel.wave.name
        val toneData = StringBuilder()
        for (note in channel.getNotes()) {
            val tone = note.tone.toChar()
            val dur = note.duration.toChar()
            toneData.append(tone)
            toneData.append(dur)
        }
        return ChannelEntity(channel.id.toString(), songId.toString(), waveName, toneData.toString())
    }
}