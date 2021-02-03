package mobile.birdie.exam1.messages.data.offlineSupport

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mobile.birdie.exam1.core.TAG
import mobile.birdie.exam1.core.Result
import mobile.birdie.exam1.messages.data.Message
import mobile.birdie.exam1.messages.data.MessagesRepository
import mobile.birdie.exam1.messages.data.remote.MessageApi

object RepoHelper {
    var messagesRepository: MessagesRepository? = null
    private var msg: Message? = null
    private var viewLifecycleOwner: LifecycleOwner? = null

    fun setRepo(repo: MessagesRepository) {
        messagesRepository = repo
    }

    fun setMsg(message: Message) {
        msg = message
    }

    fun setViewLifecycleOwner(viewLifecycleOwnerParam: LifecycleOwner) {
        viewLifecycleOwner = viewLifecycleOwnerParam
    }

    fun read(){
        viewLifecycleOwner!!.lifecycleScope.launch {
            readHelper()
        }
    }

    private suspend fun readHelper() : Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {
                msg?.let { msg?.id?.let { it1 -> MessageApi.service.readMessage(it1, it) } }

                msg?.changed = false
                msg?.let { messagesRepository?.messageDao?.update(it) }
//                Properties.instance.toastMessage.postValue("Message state was updated on the server")
                return Result.Success(true)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}