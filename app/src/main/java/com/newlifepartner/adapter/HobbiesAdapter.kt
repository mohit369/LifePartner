package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newlifepartner.R
import com.newlifepartner.modal.Data

class HobbiesAdapter (val hobbiesList: ArrayList<String>) : RecyclerView.Adapter<HobbiesAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val hobbies:TextView = itemView.findViewById(R.id.title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hobbies_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hobbiesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.hobbies.text = hobbiesList[position]
    }

}