package com.newlifepartner.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.newlifepartner.MainActivity
import com.newlifepartner.R
import com.newlifepartner.activity.ChatActivity
import com.newlifepartner.adapter.HobbiesAdapter
import com.newlifepartner.adapter.UserImageAdapter
import com.newlifepartner.databinding.FragmentUserDetailsBinding
import com.newlifepartner.modal.Image
import com.newlifepartner.modal.MatchProfile
import com.newlifepartner.modal.ResponseDashboard
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.modal.UserDetailModal
import com.newlifepartner.modal.UserRequest
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


class UserDetailsFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var userImageAdapter: UserImageAdapter
    private lateinit var hobbiesAdapter: HobbiesAdapter
    private lateinit var interestedAdapter: HobbiesAdapter
    private var username: String = " "
    private lateinit var imageList: ArrayList<Image>
    private val args by navArgs<UserDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDetailsBinding.inflate(layoutInflater)

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            checkUserRequest()
            callGetUserApi()
        }else{
            binding.progressBar.visibility = View.GONE
            binding.noDataFound.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
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

        binding.connect.setOnClickListener {
            sentConnectRequest()
        }

        binding.chat.setOnClickListener {
            val intent = Intent(requireContext(),ChatActivity::class.java)
            intent.apply {
                putExtra("UserID",args.id)
                putExtra("name",username)
                startActivity(this)
            }

        }


        return binding.root
    }

    private fun checkUserRequest() {
        val userId = MySharedPreferences.getInstance(requireContext()).getStringValue(Constant.USER_ID)
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<UserRequest> = safeApiCall { ApiService.retrofitService.getUserContact(userId,args.id) }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.detailsLayout.visibility = View.VISIBLE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status) {
                            if (response.value.data[0].status == "pending") {
                                binding.connect.visibility = View.GONE
                                binding.chat.visibility = View.GONE
                                binding.requested.visibility = View.VISIBLE
                            }else if (response.value.data[0].status == "accept"){
                                binding.connect.visibility = View.GONE
                                binding.chat.visibility = View.VISIBLE
                                binding.requested.visibility = View.GONE
                            }else if (response.value.data[0].status == "reject"){
                                binding.connect.visibility = View.VISIBLE
                                binding.chat.visibility = View.GONE
                                binding.requested.visibility = View.GONE
                            }
                        }
                    }
                    is ResultType.Error -> {

                    }
                }
            }
        }
    }

    private fun sentConnectRequest() {
        val userID = MySharedPreferences.getInstance(requireContext()).getStringValue(Constant.USER_ID)
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseSignUp> = safeApiCall { ApiService.retrofitService.connect(userID,args.id) }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.detailsLayout.visibility = View.VISIBLE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status) {
                            binding.connect.visibility = View.GONE
                            binding.requested.visibility = View.VISIBLE
                        }
                    }
                    is ResultType.Error -> {

                    }
                }
            }
        }
    }

    private fun callGetUserApi() {
        binding.progressBar.visibility = View.VISIBLE
        imageList = ArrayList()
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<UserDetailModal> = safeApiCall { ApiService.retrofitService.getUsersById(args.id) }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.detailsLayout.visibility = View.VISIBLE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            response.value.data.apply {
                                binding.username.text = profile.name
                                username = profile.name
                                binding.userAge.text = "${profile.age} Years, ${profile.city}"
                                binding.bio.text = profile.bio
                                binding.dob.text = "Date of Birth : "
                                binding.relationshipStatus.text = profile.relationshipStatus
                                binding.religion.text = profile.religion
                                binding.cast.text = profile.caste
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