package ntdalbec.sequencinator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "song")
data class Song(
    @PrimaryKey var uid: UUID,
    @ColumnInfo var name: String
)