package mobile.birdie.exam1.messages.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import mobile.birdie.exam1.messages.data.Message

@Dao
interface MessageDao {
    @Query("SELECT * from messages")
    fun getAll(): LiveData<List<Message>>

    @Query("SELECT * from messages")
    fun getAllSimple(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(msg: Message)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(msg: Message)

    @Query("SELECT * from messages where _id=:id")
    fun getMessage(id: Int) : Message

    @Query("DELETE FROM messages")
    suspend fun deleteAll()

    @Query("SELECT * from messages where sender=:sender")
    fun getMessagesBySender(sender: String) : List<Message>

    @Query("SELECT COUNT(*) from messages where sender=:sender and read=:read")
    fun countUnreadMessagesBySender(sender: String, read: Boolean = false) : Int

    @Query("SELECT DISTINCT sender from messages")
    fun getUsers() : List<String>

    @Query("SELECT created from messages where sender = :sender ORDER BY created DESC LIMIT 1")
    fun getLastMessageDateFromUser(sender: String) : Long
}