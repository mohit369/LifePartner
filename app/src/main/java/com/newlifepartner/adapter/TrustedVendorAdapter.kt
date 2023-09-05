package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newlifepartner.R
import com.newlifepartner.fragment.DashboardFragmentDirections
import com.newlifepartner.modal.Vendor

class TrustedVendorAdapter(val list: ArrayList<Vendor>,val navController: NavController) :
    RecyclerView.Adapter<TrustedVendorAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name:TextView = itemView.findViewById(R.id.trusted_name)
        val image: ImageView = itemView.findViewById(R.id.trusted_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trusted_vendor_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!list[position].vendorPhoto.isNullOrEmpty()) {
            Glide.with(holder.itemView.context).load(list[position].vendorPhoto).into(holder.image)
        }
        list[position].name?.let { holder.name.text = it }

        holder.itemView.setOnClickListener {
            val action = DashboardFragmentDirections.actionActionHomeToVendorDetailFragment(list[position].id)
            navController.navigate(action)
        }
    }
}