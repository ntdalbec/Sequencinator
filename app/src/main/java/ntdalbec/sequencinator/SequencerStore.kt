package ntdalbec.sequencinator

import java.util.*

interface SequencerStore {
    val songs: Array<Song>

    fun addSong(name: String): Song

    fun deleteSong(song: Song)

    fun updateSong(song: Song)

    fun getChannelsBySong(id: UUID): List<Channel>

    fun addChannel(channel: Channel, songId: UUID)

    fun updateChannel(channel: Channel, songId: UUID)

    fun deleteChannel(channel: Channel, songId: UUID)
}