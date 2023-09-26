package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newlifepartner.R
import com.newlifepartner.fragment.DashboardFragmentDirections
import com.newlifepartner.modal.Users

class VerifiedMatesAdapter(val list: ArrayList<Users>,val navController: NavController) :
    RecyclerView.Adapter<VerifiedMatesAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name:TextView = itemView.findViewById(R.id.verified_name)
        val image:ImageView = itemView.findViewById(R.id.verified_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.verified_mates_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!list[position].photo.isNullOrEmpty()) {
            Glide.with(holder.itemView.context).load(list[position].photo).into(holder.image)
        }else{
            holder.image.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.context.resources,R.drawable.img_front,null))
        }
        list[position].name?.let { holder.name.text = it }

        holder.itemView.setOnClickListener {
            val action = DashboardFragmentDirections.actionActionHomeToUserDetailsFragment(list[position].id)
            navController.navigate(action)
        }

    }
}