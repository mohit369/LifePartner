package com.newlifepartner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.newlifepartner.MainActivity
import com.newlifepartner.R
import com.newlifepartner.adapter.CountrySpinnerAdapter
import com.newlifepartner.adapter.VendorFragmentAdapter
import com.newlifepartner.databinding.CitySearchDialogBinding
import com.newlifepartner.databinding.FragmentVendorBinding
import com.newlifepartner.modal.ResponseDashboard
import com.newlifepartner.modal.UserDetailModal
import com.newlifepartner.modal.Vendor
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VendorFragment : Fragment() {

    private var selectedCountryVendor: String = ""
    private lateinit var binding:FragmentVendorBinding
    private var vendorList: ArrayList<Vendor> = ArrayList()
    private lateinit var vendorAdapter:VendorFragmentAdapter
    private val args by navArgs<VendorFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVendorBinding.inflate(layoutInflater)

        vendorAdapter = VendorFragmentAdapter(vendorList,findNavController())
        binding.vendorRv.adapter = vendorAdapter

        return binding.root
    }

    private fun callGetUserDetailApi() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseDashboard<Vendor>> = safeApiCall { ApiService.retrofitService.getSearchVendor("0","100",args.category,selectedCountryVendor) }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            vendorList = response.value.data as ArrayList<Vendor>
                            if (vendorList.isEmpty()){
                                binding.vendorRv.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            }else{
                                binding.vendorRv.visibility = View.VISIBLE
                                binding.noDataFound.visibility = View.GONE
                            }
                            vendorAdapter.updateList(vendorList)
                        }else{
                            binding.vendorRv.visibility = View.GONE
                            binding.noDataFound.visibility = View.VISIBLE
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

    override fun onStart() {
        super.onStart()
        if (args.showDialog) {
            showVendorCityDialog()
        }else{
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                selectedCountryVendor = args.city
                callGetUserDetailApi()
            }else{
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showVendorCityDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val binding = CitySearchDialogBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        builder.setCancelable(false)
        val cityList = (requireActivity() as MainActivity).cityList

        val cityAdapter = CountrySpinnerAdapter(requireContext(),cityList)
        binding.citySpinner.adapter = cityAdapter



        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCountryVendor = cityList[position].id

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.btnSearch.setOnClickListener {

            if (selectedCountryVendor.isEmpty()){
                Toast.makeText(requireContext(), "Please fill all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                callGetUserDetailApi()
            }else{
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }

        }

        dialog.show()

    }


}