package mobile.birdie.exam1.messages.data

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import mobile.birdie.exam1.messages.data.local.MessageDao
import mobile.birdie.exam1.core.Result
import mobile.birdie.exam1.core.TAG
import mobile.birdie.exam1.messages.data.remote.MessageApi
import java.lang.Exception

class MessagesRepository(val messageDao: MessageDao) {
    var messages = MediatorLiveData<List<Message>>().apply { postValue(emptyList()) }

    suspend fun refresh(): Result<Boolean> {
        return try {
            if (Properties.instance.internetActive.value!!) {
                val messagesApi = MessageApi.service.find()
                messages.value = messagesApi
                for (msg in messagesApi) {
                    if(messageDao.getMessage(msg.id) == null) {
                        msg.changed = false
                        messageDao.insert(msg)
                    }
                }
            } else {
                messages.addSource(messageDao.getAll()) {
                    messages.value = it
                }
            }
            Result.Success(true)
        } catch (e: Exception) {
            messages.addSource(messageDao.getAll()) {
                messages.value = it
            }
            Result.Error(e)
        }
    }

    suspend fun readMessage(id: Int): Result<Boolean> {
        return try {
            val readMessage = messageDao.getMessage(id)
            readMessage.read = true
            if (Properties.instance.internetActive.value!!) {
                MessageApi.service.readMessage(id, readMessage)
                readMessage.changed = false
                messageDao.update(readMessage)
                Log.d(TAG, "updating online $readMessage")
            } else {
                readMessage.changed = true
                Properties.instance.toastMessage.postValue("Messages were read but request will be sent when online")
                messageDao.update(readMessage)
                Log.d(TAG, "updating offline $readMessage")
            }
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getUsers(): List<String> {
        return messageDao.getUsers()
    }

    fun getNrOfUnreadMsgs(sender: String): Int {
        return messageDao.countUnreadMessagesBySender(sender)
    }

    fun getDateOfLastMessageFromUser(sender: String): Long {
        return messageDao.getLastMessageDateFromUser(sender)
    }

    fun getMessagesByUser(sender: String): List<Message> {
        return messageDao.getMessagesBySender(sender)
    }

    fun getAll() {
        messages.addSource(messageDao.getAll()) {
            messages.value = it
        }
    }
}