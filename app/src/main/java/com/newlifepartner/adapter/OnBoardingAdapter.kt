package com.newlifepartner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.newlifepartner.databinding.OnboardingItemBinding
import com.newlifepartner.modal.OnBoardingItem

class OnBoardingAdapter(
    private val mList: List<OnBoardingItem>
) : RecyclerView.Adapter<OnBoardingAdapter.ItemOnBoardingHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnBoardingAdapter.ItemOnBoardingHolder {
        val binding = OnboardingItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemOnBoardingHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemOnBoardingHolder, position: Int) {
        with(holder) {
            val onBoarding = mList[position]
            binding.title.text = onBoarding.title
            binding.description.text = onBoarding.description
            binding.onboardingImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context,onBoarding.image))
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ItemOnBoardingHolder(val binding: OnboardingItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}