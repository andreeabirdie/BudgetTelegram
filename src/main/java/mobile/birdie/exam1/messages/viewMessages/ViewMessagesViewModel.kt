package mobile.birdie.exam1.messages.viewMessages

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mobile.birdie.exam1.core.Result
import mobile.birdie.exam1.core.TAG
import mobile.birdie.exam1.messages.data.Message
import mobile.birdie.exam1.messages.data.MessageDTO
import mobile.birdie.exam1.messages.data.MessagesRepository
import mobile.birdie.exam1.messages.data.local.MessageDatabase
import mobile.birdie.exam1.messages.data.remote.RemoteDataSource
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ViewMessagesViewModel(
    application: Application
) : AndroidViewModel(application) {
    val messagesRepository: MessagesRepository
    val messages: LiveData<List<Message>>
    private lateinit var username: String

    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    init {
        val messageDao = MessageDatabase.getDatabase(application).msgDao()
        messagesRepository = MessagesRepository(messageDao)
        messages = messagesRepository.messages

        val request = Request.Builder().url("ws://192.168.0.104:3000").build()
        OkHttpClient().newWebSocket(
            request,
            RemoteDataSource.MyWebSocketListener(application.applicationContext)
        )
        CoroutineScope(Dispatchers.Main).launch { collectEvents() }
    }

    fun setUsername(name: String) {
        username = name
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = messagesRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }

    fun readMessage(id: Int) {
        viewModelScope.launch {
            Log.v(TAG, "reading message $id...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = messagesRepository.readMessage(id)) {
                is Result.Success -> {
                    Log.d(TAG, "read succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "read failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }

    private suspend fun collectEvents() {
        while (true) {
            val res = JSONObject(RemoteDataSource.eventChannel.receive())
            val msg = Gson().fromJson(res.toString(), MessageDTO::class.java)
            Log.d("ws", "received $res")
            if (msg.sender == username) {
                messagesRepository.messageDao.insert(
                    Message(
                        msg.id,
                        msg.text,
                        msg.read,
                        msg.sender,
                        msg.created,
                        false
                    )
                )
                messagesRepository.getAll()
            }
        }
    }
}