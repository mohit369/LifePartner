package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newlifepartner.R
import com.newlifepartner.modal.Data
import com.newlifepartner.modal.DataX

class FindWeddingVendorAdapter(val vendorClick: VendorClick) : RecyclerView.Adapter<FindWeddingVendorAdapter.ViewHolder>() {

    private var list:ArrayList<DataX> = ArrayList()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val icon = itemView.findViewById<ImageView>(R.id.icon)
        val title = itemView.findViewById<TextView>(R.id.title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.find_wedding_vendor_layout,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!list[position].image.isNullOrEmpty()) {
            Glide.with(holder.itemView).load(list[position].image).into(holder.icon)
        }
        list[position].name?.let { holder.title.text = it }

        holder.itemView.setOnClickListener {
            vendorClick.vendorClick(list[position].id)
        }

    }

    fun updateList(list: ArrayList<DataX>){
        this.list = list
        notifyDataSetChanged()
    }

    interface VendorClick{
        fun vendorClick(id: String)
    }

}