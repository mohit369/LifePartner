package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newlifepartner.R
import com.newlifepartner.databinding.MatesItemLayoutBinding
import com.newlifepartner.databinding.VendorItemLayoutBinding
import com.newlifepartner.fragment.MatchFragmentDirections
import com.newlifepartner.fragment.VendorFragmentDirections
import com.newlifepartner.modal.MatchProfile
import com.newlifepartner.modal.Vendor

class VendorFragmentAdapter(var list: ArrayList<Vendor>, val navController: NavController):RecyclerView.Adapter<VendorFragmentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding:VendorItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = VendorItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ratingBar.isEnabled = false
        list[position].name?.let {  holder.binding.profileName.text = it }
        list[position].category?.let {  holder.binding.category.text = it }
        holder.binding.details.text = ", ${list[position].city} | ${list[position]._package}"
        list[position].vendorPhoto?.let {  Glide.with(holder.itemView.context).load(it).into(holder.binding.image)}

        holder.itemView.setOnClickListener {
            val action = VendorFragmentDirections.actionActionVendorToVendorDetailFragment(list[position].id)
            navController.navigate(action)
        }

    }

    fun updateList(matchList: java.util.ArrayList<Vendor>) {
        list = matchList
        notifyDataSetChanged()
    }
}