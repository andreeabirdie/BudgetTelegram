package mobile.birdie.exam1.messages.viewMessages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_list.*
import mobile.birdie.exam1.R
import mobile.birdie.exam1.core.TAG
import mobile.birdie.exam1.messages.data.User
import mobile.birdie.exam1.messages.data.remote.MessageApi

class ViewMessagesFragment : Fragment() {
    private lateinit var viewModel: ViewMessagesViewModel
    private lateinit var msgAdapter: MessagesAdapter
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = arguments?.getParcelable("user")
        fragmentTitle.text = "user ${user?.name}"
        setUpMsgsList()
    }

    private fun setUpMsgsList(){
        viewModel = ViewModelProvider(this).get(ViewMessagesViewModel::class.java)
        user?.name?.let { viewModel.setUsername(it) }
        msgAdapter = MessagesAdapter(this)
        itemsList.adapter = msgAdapter

        viewModel.messages.observe(viewLifecycleOwner) { items ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${items.size}")
            msgAdapter.messages = items.filter { msg -> msg.sender == user?.name }
            for(msg in msgAdapter.messages.filter { msg -> !msg.read }){
                viewModel.readMessage(msg.id)
            }
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
}