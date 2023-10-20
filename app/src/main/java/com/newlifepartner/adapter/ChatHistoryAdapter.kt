package com.newlifepartner.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newlifepartner.R
import com.newlifepartner.activity.ChatActivity
import com.newlifepartner.databinding.ChatHistoryItemLayoutBinding
import com.newlifepartner.databinding.NotificationItemLayBinding
import com.newlifepartner.modal.ChatHistoryModal

class ChatHistoryAdapter(var list: ArrayList<ChatHistoryModal.Data>):RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding:ChatHistoryItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ChatHistoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var data =  list[position]
        holder.binding.title.text = data.name
        holder.binding.msg.text = data.lastMessage
        if (!data.image.isNullOrEmpty()){
            Glide.with(holder.itemView.context).load(data.image).into(holder.binding.userImage)
        }else{
            Glide.with(holder.itemView.context).load(R.drawable.img_front).into(holder.binding.userImage)
        }

        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context,ChatActivity::class.java).putExtra("UserID",list[position].userId)
                .putExtra("name",list[position].name))
        }

    }

    fun updateList(matchList: ArrayList<ChatHistoryModal.Data>) {
        list = matchList
        notifyDataSetChanged()
    }
}