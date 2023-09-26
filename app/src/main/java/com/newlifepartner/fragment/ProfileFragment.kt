package com.newlifepartner.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.newlifepartner.MainActivity
import com.newlifepartner.R
import com.newlifepartner.activity.LoginActivity
import com.newlifepartner.adapter.HobbiesAdapter
import com.newlifepartner.adapter.UserImageAdapter
import com.newlifepartner.databinding.FragmentProfileBinding
import com.newlifepartner.modal.DataXX
import com.newlifepartner.modal.Image
import com.newlifepartner.modal.UserDetailModal
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.Constant
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.MySharedPreferences
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var preferences:MySharedPreferences
    private lateinit var userImageAdapter: UserImageAdapter
    private lateinit var hobbiesAdapter: HobbiesAdapter
    private lateinit var interestedAdapter: HobbiesAdapter
    private lateinit var imageList:ArrayList<Image>
    private var data:DataXX? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        preferences = MySharedPreferences.getInstance(requireContext())

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            callGetUserApi()
        }else{
            binding.progressBar.visibility = View.GONE
            binding.noDataFound.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }

        binding.edit.setOnClickListener {
            val action = ProfileFragmentDirections.actionActionProfileToEditProfileFragment(data)
            findNavController().navigate(action)
        }

        binding.imageBack.setOnClickListener {
            if (binding.imageViewPager.currentItem >= 0){
                binding.imageViewPager.currentItem = binding.imageViewPager.currentItem - 1
            }
        }

        binding.imageForward.setOnClickListener {
            if (::imageList.isInitialized) {
                if (binding.imageViewPager.currentItem <= imageList.size) {
                    binding.imageViewPager.currentItem = binding.imageViewPager.currentItem + 1
                }
            }
        }

        binding.signOut.setOnClickListener {
            preferences.saveBooleanValue(Constant.KEY_LOGIN,false)
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        return binding.root
    }

    private fun callGetUserApi() {
        val userId = preferences.getStringValue(Constant.USER_ID)
        imageList = ArrayList()
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<UserDetailModal> = safeApiCall { ApiService.retrofitService.getUsersById(userId) }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.detailsLayout.visibility = View.VISIBLE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            response.value.data.apply {
                                data = this
                                binding.username.text = profile.name
                                binding.userAge.text = "${profile.age} Years, ${profile.city}"
                                binding.bio.text = profile.bio
                                binding.dob.text = "Date of Birth : "
                                binding.relationshipStatus.text = profile.relationshipStatus
                                binding.religion.text = profile.religion
                                binding.cast.text = profile.caste
                                binding.interestedProfile.text = profile.interest
                                binding.hobbiesProfile.text = profile.hobbies
                                imageList.addAll(profile.images)
                                userImageAdapter = UserImageAdapter(imageList)
                                binding.imageViewPager.adapter = userImageAdapter
                                val hbList = profile.hobbies?.split(",")?.toList()?: ArrayList()
                                val interestedList = profile.interest?.split(",")?.toList()?: ArrayList()
                                hobbiesAdapter = HobbiesAdapter(hbList as ArrayList<String>)
                                interestedAdapter = HobbiesAdapter(interestedList as ArrayList<String>)
                                binding.hobbiesRv.adapter = hobbiesAdapter
                                binding.interestedRv.adapter = interestedAdapter
                            }
                        }else{
                            binding.noDataFound.visibility = View.VISIBLE
                        }
                    }
                    is ResultType.Error -> {
                        binding.noDataFound.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setBackIcon()
    }

}