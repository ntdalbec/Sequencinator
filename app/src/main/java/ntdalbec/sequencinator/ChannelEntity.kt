package ntdalbec.sequencinator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Song::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("song_id"),
        onDelete = CASCADE)],
    tableName = "channel"
)
data class ChannelEntity(
    @PrimaryKey var uid: String,
    @ColumnInfo(name = "song_id") var songId: String,
    @ColumnInfo(name = "wave_name") var waveName: String,
    @ColumnInfo(name = "note_data") var noteData: String?
)