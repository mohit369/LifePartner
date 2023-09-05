package com.newlifepartner.fragment

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
import com.newlifepartner.adapter.HistoryAdapter
import com.newlifepartner.databinding.FragmentHistoryBinding
import com.newlifepartner.modal.City
import com.newlifepartner.modal.DataXXXX
import com.newlifepartner.modal.HistoryModal
import com.newlifepartner.modal.ResponseDashboard
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


class HistoryFragment : Fragment() {

    private lateinit var binding:FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private var historyList:ArrayList<DataXXXX> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        historyAdapter = HistoryAdapter(historyList,findNavController())

        binding.historyRv.adapter = historyAdapter

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            callHistoryAPI()
        }else{
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }

        
        return binding.root
    }

    private fun callHistoryAPI() {
        val id = MySharedPreferences.getInstance(requireContext()).getStringValue(Constant.USER_ID)
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<HistoryModal> = safeApiCall { ApiService.retrofitService.fetchVendorLead(id) }
            withContext(Dispatchers.Main) {
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            historyList.addAll(response.value.data)
                            historyAdapter.updateList(historyList)
                        }
                    }
                    is ResultType.Error -> {

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