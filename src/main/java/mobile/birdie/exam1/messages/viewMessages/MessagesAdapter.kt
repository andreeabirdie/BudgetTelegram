package mobile.birdie.exam1.messages.viewMessages

import android.animation.ValueAnimator
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_message.view.*
import mobile.birdie.exam1.R
import mobile.birdie.exam1.core.TAG
import mobile.birdie.exam1.messages.data.Message
import mobile.birdie.exam1.messages.data.remote.MessageApi
import mobile.birdie.exam1.messages.usersList.UsersListFragment


class MessagesAdapter(private val messagesFragment: ViewMessagesFragment) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    var messages = emptyList<Message>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_message, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount() = messages.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageDate: TextView = view.messageDate
        private val messageText: TextView = view.messageText

        fun bind(holder: ViewHolder, position: Int) {
            val msg = messages[position]

            with(holder) {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = java.util.Date(msg.created)
                messageDate.text = "[${sdf.format(date)}]:"
                messageText.text = msg.text
                if(!msg.read) {
                    messageText.setTypeface(null, BOLD)
                    val size = 1F
                    val animationDuration: Long = 1
                    val animator = ValueAnimator.ofFloat(size, size)
                    animator.duration = animationDuration.toLong()
                    animator.startDelay = 1000

                    animator.addUpdateListener {
                        messageText.setTypeface(null, NORMAL)
                        msg.read = true
                    }

                    animator.start()
                }
            }
        }
    }
}