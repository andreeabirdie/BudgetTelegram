package mobile.birdie.exam1.messages.usersList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_user_msg.view.*
import mobile.birdie.exam1.R
import mobile.birdie.exam1.core.TAG
import mobile.birdie.exam1.messages.data.MessagesRepository
import mobile.birdie.exam1.messages.data.User

class UsersListAdapter(private val usersListFragment: UsersListFragment) :
    RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {

    var users = emptyList<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onUserClick: View.OnClickListener

    init{
        onUserClick = View.OnClickListener {view ->
            val user = view.tag as User
            Log.d(TAG,"clicked ${user.name}")
            usersListFragment.findNavController()
                .navigate(
                    R.id.action_usersListFragment_to_viewMessagesFragment,
                    bundleOf("user" to user)
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_user_msg, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userId: TextView = view.userId
        private val unreadMessage: TextView = view.userUnreadMsg

        fun bind(holder: ViewHolder, position: Int) {
            val user = users[position]
            Log.d(TAG, "added ${user.name} - [${user.unreadMsg}]")

            with(holder) {
                itemView.tag = user
                userId.text = "user: ${user.name}"
                if(user.unreadMsg > 0) unreadMessage.text = "[${user.unreadMsg} unread messages]"
                itemView.setOnClickListener(onUserClick)
            }
        }
    }
}