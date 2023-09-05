package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        list[position].name?.let {  holder.binding.profileName.text = it }
        list[position].age?.let {  holder.binding.profileAge.text = "$it Years, ${list[position].city}" }
        list[position].hobbies?.let {  holder.binding.hobbies.text = it }
        list[position].photo?.let {  Glide.with(holder.itemView.context).load(list[position].photo).into(holder.binding.profileImage)}

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