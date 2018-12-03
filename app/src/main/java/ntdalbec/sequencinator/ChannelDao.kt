package ntdalbec.sequencinator

import androidx.room.*
import java.util.*

@Dao
interface ChannelDao {
    @Insert
    fun insertChannels(vararg channels: ChannelEntity)

    @Update
    fun updateChannels(vararg channels: ChannelEntity)

    @Delete
    fun deleteChannels(vararg channels: ChannelEntity)

    @Query("SELECT * FROM channel")
    fun loadAllChannels(): Array<ChannelEntity>

    @Query("SELECT * FROM channel WHERE song_id = :songId")
    fun loadChannelsBySong(songId: UUID): Array<ChannelEntity>

    @Query("SELECT * FROM channel WHERE uid = :id LIMIT 1")
    fun loadChannelById(id: UUID): ChannelEntity
}