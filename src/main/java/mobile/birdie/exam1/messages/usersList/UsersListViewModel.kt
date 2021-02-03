package mobile.birdie.exam1.messages.usersList

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.PrimitiveKind
import mobile.birdie.exam1.core.TAG
import mobile.birdie.exam1.core.Result
import mobile.birdie.exam1.messages.data.Message
import mobile.birdie.exam1.messages.data.MessageDTO
import mobile.birdie.exam1.messages.data.MessagesRepository
import mobile.birdie.exam1.messages.data.User
import mobile.birdie.exam1.messages.data.local.MessageDatabase
import mobile.birdie.exam1.messages.data.offlineSupport.RepoHelper
import mobile.birdie.exam1.messages.data.remote.RemoteDataSource
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class UsersListViewModel(application: Application) : AndroidViewModel(application) {

    val messagesRepository: MessagesRepository
    var messages: LiveData<List<Message>>
    var users: MutableLiveData<List<User>> = MutableLiveData()

    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    init{
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

    fun refresh(){
        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
//            db amnesia
//            messagesRepository.messageDao.deleteAll()
            when (val result = messagesRepository.refresh()) {
                is Result.Success -> {
                    updateUsersList()
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

    private fun updateUsersList(){
        val sortedUsers = messagesRepository.getUsers().sortedByDescending { user -> messagesRepository.getDateOfLastMessageFromUser(user) }
        val newUserList = mutableListOf<User>()
        for(user in sortedUsers){
            newUserList.add(User(user, messagesRepository.getNrOfUnreadMsgs(user)))
        }
        users.value = newUserList

        RepoHelper.setRepo(messagesRepository)
    }

    private suspend fun collectEvents() {
        while (true) {
            val res = JSONObject(RemoteDataSource.eventChannel.receive())
            val msg = Gson().fromJson(res.toString(), MessageDTO::class.java)
            Log.d("ws", "received $msg")
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
            updateUsersList()
        }
    }
}