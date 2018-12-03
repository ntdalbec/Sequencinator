package ntdalbec.sequencinator

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChannelEntity::class, Song::class],
    version = 1)
abstract class SongDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun songDao(): SongDao
}