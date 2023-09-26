package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newlifepartner.BuildConfig
import com.newlifepartner.R
import com.newlifepartner.databinding.MatesItemLayoutBinding
import com.newlifepartner.fragment.MatchFragmentDirections
import com.newlifepartner.modal.MatchProfile

class MatchFragmentAdapter(var list: ArrayList<MatchProfile>, val navController: NavController):RecyclerView.Adapter<MatchFragmentAdapter.ViewHolder>() {

   inner class ViewHolder(val binding:MatesItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MatesItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (list[position].name != null && !list[position].name.isNullOrBlank()) {
            holder.binding.profileName.text = list[position].name
        }
        if (list[position].age != null && !list[position].age.isNullOrBlank()) {
            holder.binding.profileAge.text = "${list[position].age} Years, ${list[position].city}"
        }
        if (list[position].hobbies != null && !list[position].hobbies.isNullOrBlank()) {
            holder.binding.hobbies.text = list[position].hobbies
        }
        if (list[position].photo != null && !list[position].photo.isNullOrBlank()) {
                Glide.with(holder.itemView.context).load(list[position].photo)
                    .into(holder.binding.profileImage)
        }else{
            holder.binding.profileImage.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.context.resources,R.drawable.img_front,null))
        }


        holder.itemView.setOnClickListener {
            val action = MatchFragmentDirections.actionActionMatchToUserDetailsFragment(list[position].id)
            navController.navigate(action)
        }

    }

    fun updateList(matchList: java.util.ArrayList<MatchProfile>) {
        list = matchList
        notifyDataSetChanged()
    }
}