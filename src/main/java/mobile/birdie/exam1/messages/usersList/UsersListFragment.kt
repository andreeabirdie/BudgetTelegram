package mobile.birdie.exam1.messages.usersList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.fragment_list.*
import mobile.birdie.exam1.R
import mobile.birdie.exam1.core.TAG
import mobile.birdie.exam1.messages.data.offlineSupport.MessagesRepoWorker
import mobile.birdie.exam1.messages.data.offlineSupport.RepoHelper


class UsersListFragment : Fragment() {
    private lateinit var viewModel: UsersListViewModel
    private lateinit var userListAdapter: UsersListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setUpMsgsList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentTitle.setText(R.string.users_list_unread_messages)

        RepoHelper.setViewLifecycleOwner(viewLifecycleOwner)
        Properties.instance.internetActive.observe(viewLifecycleOwner, Observer {
            if(Properties.instance.internetActive.value == true) {
                Log.d(TAG, "sending offline actions to server")
                sendOfflineActionsToServer()
            }
        })
    }

    private fun setUpMsgsList() {
        viewModel = ViewModelProvider(this).get(UsersListViewModel::class.java)
        userListAdapter = UsersListAdapter(this)
        itemsList.adapter = userListAdapter

        viewModel.users.observe(viewLifecycleOwner) { items ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${items.size}")
            userListAdapter.users = items
            Log.d(TAG, "added items ${userListAdapter.users}")
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loadingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                Log.d(TAG, "Loading exception ${exception.message}")
            }
        }
        viewModel.refresh()
    }

    private fun sendOfflineActionsToServer() {
        viewModel.messagesRepository.messageDao.getAllSimple().forEach { msg ->
            if (msg.changed) {
                RepoHelper.setMsg(msg)
                Log.d("qwerty", "${msg.id} will pe updated on the server")
                var dataParam = Data.Builder().putString("operation", "read")

                val request = OneTimeWorkRequestBuilder<MessagesRepoWorker>()
                    .setInputData(dataParam.build())
                    .build()
                WorkManager.getInstance(requireContext()).enqueue(request)
            }
        }
        viewModel.refresh()
    }
}