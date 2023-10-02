package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newlifepartner.databinding.NotificationItemLayBinding
import com.newlifepartner.modal.NotificationModel

class NotificationAdapter(var list: ArrayList<NotificationModel.Data>):RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding:NotificationItemLayBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = NotificationItemLayBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var data =  list[position]
        holder.binding.title.text = data.title
        holder.binding.msg.text = data.msg

    }

    fun updateList(matchList: ArrayList<NotificationModel.Data>) {
        list = matchList
        notifyDataSetChanged()
    }
}