package com.newlifepartner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.newlifepartner.R
import com.newlifepartner.modal.City

class CountrySpinnerAdapter(private val context: Context, private val countryList: List<City>) : ArrayAdapter<City>(context, 0, countryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.city_layout, parent, false)

        val countryImageView: ImageView = view.findViewById(R.id.countryImageView)
        val countryNameTextView: TextView = view.findViewById(R.id.countryNameTextView)


        Glide.with(context).load(countryList[position].images).into(countryImageView)
        countryNameTextView.text = countryList[position].city

        return view
    }
}
