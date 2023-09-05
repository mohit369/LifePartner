package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.newlifepartner.R
import com.newlifepartner.modal.Data

class SliderAdapter(val imageList: ArrayList<Data>,val viewPager2: ViewPager2) : RecyclerView.Adapter<SliderAdapter.ViewHolder>() {


    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        val imageView = itemView.findViewById<ImageView>(R.id.image_slider)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
     val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_image_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView).load(imageList[position].images).into(holder.imageView)
        if (position == imageList.size-1){
            viewPager2.post(runnable)
        }
    }

    private val runnable = Runnable {
        imageList.addAll(imageList)
        notifyDataSetChanged()
    }
}