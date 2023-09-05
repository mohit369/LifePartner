package com.newlifepartner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newlifepartner.R
import com.newlifepartner.modal.ChatModal
import com.newlifepartner.utils.Constant
import com.newlifepartner.utils.MySharedPreferences

class ChatAdapter(val context: Context,private val messages: ArrayList<ChatModal>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENT_MESSAGE = 1
    private val RECEIVED_MESSAGE = 2
    private var userId = MySharedPreferences.getInstance(context).getStringValue(Constant.USER_ID)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SENT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_sent_layout, parent, false)
                SentMessageViewHolder(view)
            }
            RECEIVED_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_layout, parent, false)
                ReceivedMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bindSentMessage(message)
            is ReceivedMessageViewHolder -> holder.bindReceivedMessage(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].from_user == userId) SENT_MESSAGE else RECEIVED_MESSAGE
    }

    public fun updateList(list: ArrayList<ChatModal>){
        messages.clear()
        messages.addAll(list)
        notifyDataSetChanged()
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageSent: TextView = itemView.findViewById(R.id.message_sent)
        fun bindSentMessage(message: ChatModal) {
          messageSent.text = message.message
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageReceive: TextView = itemView.findViewById(R.id.message_receive)
        fun bindReceivedMessage(message: ChatModal) {
            messageReceive.text = message.message
        }
    }

}
