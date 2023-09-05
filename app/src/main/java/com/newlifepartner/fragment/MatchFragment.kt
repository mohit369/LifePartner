package com.newlifepartner.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.newlifepartner.MainActivity
import com.newlifepartner.R
import com.newlifepartner.activity.OtpActivity
import com.newlifepartner.adapter.MatchFragmentAdapter
import com.newlifepartner.databinding.FragmentMatchBinding
import com.newlifepartner.modal.MatchProfile
import com.newlifepartner.modal.ResponseDashboard
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MatchFragment : Fragment() {

    private lateinit var binding:FragmentMatchBinding
    private val argument by navArgs<MatchFragmentArgs>()
    private var matchList: ArrayList<MatchProfile> = ArrayList()
    private lateinit var matchFragmentAdapter: MatchFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchBinding.inflate(layoutInflater)

        matchFragmentAdapter = MatchFragmentAdapter(matchList,findNavController())
        binding.matchRv.adapter = matchFragmentAdapter

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            callMatchUsersAPI()
        }else{
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun callMatchUsersAPI() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseDashboard<MatchProfile>> = safeApiCall { ApiService.retrofitService.getSearchUsers("0","10",argument.gender,argument.age,argument.city) }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                           matchList  = response.value.data as ArrayList<MatchProfile>
                            if (matchList.isEmpty()){
                                binding.matchRv.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            }else{
                                binding.matchRv.visibility = View.VISIBLE
                                binding.noDataFound.visibility = View.GONE
                            }
                            matchFragmentAdapter.updateList(matchList)
                        }
                    }
                    is ResultType.Error -> {
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