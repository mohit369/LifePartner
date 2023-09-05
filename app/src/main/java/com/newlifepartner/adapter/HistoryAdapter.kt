package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newlifepartner.BuildConfig
import com.newlifepartner.R
import com.newlifepartner.databinding.HistoryLayoutBinding
import com.newlifepartner.databinding.MatesItemLayoutBinding
import com.newlifepartner.fragment.MatchFragmentDirections
import com.newlifepartner.modal.DataXXXX
import com.newlifepartner.modal.MatchProfile

class HistoryAdapter(var list: ArrayList<DataXXXX>, val navController: NavController):RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding:HistoryLayoutBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = HistoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var data =  list[position]
        holder.binding.profileName.text = data.name
        holder.binding.budget.text = data.budget
        holder.binding.functionDate.text = data.function_date
        holder.binding.email.text = data.email
        holder.binding.contactNo.text = data.contact_number
       // list[position].age?.let {  holder.binding.profileAge.text = "$it Years, ${list[position].city}" }
        //list[position].hobbies?.let {  holder.binding.hobbies.text = it }

       /* holder.itemView.setOnClickListener {
            val action = MatchFragmentDirections.actionActionMatchToUserDetailsFragment(list[position].id)
            navController.navigate(action)
        }*/

    }

    fun updateList(matchList: java.util.ArrayList<DataXXXX>) {
        list = matchList
        notifyDataSetChanged()
    }
}