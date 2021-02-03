package mobile.birdie.exam1.messages.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mobile.birdie.exam1.messages.data.Message

@Database(entities = [Message::class], version = 2)
abstract class MessageDatabase : RoomDatabase() {

    abstract fun msgDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDatabase? = null

        fun getDatabase(context: Context): MessageDatabase {
            val inst = INSTANCE
            if (inst != null) {
                return inst
            }
            val instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    MessageDatabase::class.java,
                    "msg_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            INSTANCE = instance
            return instance
        }
    }
}
