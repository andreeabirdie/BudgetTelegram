package mobile.birdie.exam1.messages.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey @ColumnInfo(name = "_id") val id: Int,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "read") var read: Boolean,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "created") val created: Long,
    @ColumnInfo(name = "wasChangedOffline") var changed: Boolean
)


data class MessageDTO(
    val id: Int,
    val text: String,
    var read: Boolean,
    val sender: String,
    val created: Long
)