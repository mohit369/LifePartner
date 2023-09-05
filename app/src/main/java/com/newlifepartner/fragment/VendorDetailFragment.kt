package com.newlifepartner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.newlifepartner.MainActivity
import com.newlifepartner.R
import com.newlifepartner.adapter.UserImageAdapter
import com.newlifepartner.databinding.ConnectToVendorBinding
import com.newlifepartner.databinding.FragmentVendorDetailBinding
import com.newlifepartner.databinding.SearchDialogBinding
import com.newlifepartner.modal.City
import com.newlifepartner.modal.GetVendorDetailModal
import com.newlifepartner.modal.Image
import com.newlifepartner.modal.ResponseDashboard
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.modal.UserDetailModal
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VendorDetailFragment : Fragment() {

    private lateinit var binding:FragmentVendorDetailBinding
    private lateinit var userImageAdapter: UserImageAdapter
    private val args by navArgs<VendorDetailFragmentArgs>()
    private lateinit var imageList: ArrayList<Image>
    private var cat = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVendorDetailBinding.inflate(layoutInflater)
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            callGetVendorApi()
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
            if (binding.imageViewPager.currentItem <= imageList.size){
                binding.imageViewPager.currentItem = binding.imageViewPager.currentItem + 1
            }
        }

        binding.contactBtn.setOnClickListener {
            openConnectForm()
        }

        return binding.root
    }

    private fun openConnectForm() {
        val builder = AlertDialog.Builder(requireContext())
        val binding = ConnectToVendorBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        builder.setCancelable(true)

        binding.btnSend.setOnClickListener {
            val name = binding.nameEdt.text.toString()
            val contactNo = binding.contactEdt.text.toString()
            val email = binding.emailEdt.text.toString()
            val function = binding.functionEdt.text.toString()
            val budget = binding.budgetEdt.text.toString()
            val vendorId = args.id

            if (name.isEmpty() || contactNo.isEmpty() || email.isEmpty() || function.isEmpty() || budget.isEmpty()){
                Toast.makeText(requireContext(), "Please enter all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
                val response : ResultType<ResponseSignUp> = safeApiCall { ApiService.retrofitService.sendVendorLead(vendorId,name,contactNo,email,function,budget,cat) }
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    when (response) {
                        is ResultType.Success -> {
                            if (response.value.status){
                                Toast.makeText(requireContext(), response.value.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        is ResultType.Error -> {
                        }
                    }
                }
            }

        }

        dialog.show()
    }

    private fun callGetVendorApi() {
        binding.progressBar.visibility = View.VISIBLE
        imageList = ArrayList()
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<GetVendorDetailModal> = safeApiCall { ApiService.retrofitService.getVendorById(args.id) }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.detailsLayout.visibility = View.VISIBLE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            response.value.data.apply {
                                binding.username.text = vendors.name
                                binding.cat.text = "${vendors.category}, "
                                binding.city.text = vendors.city
                                binding.bio.text = vendors.aboutBus
                                binding.link.text = vendors.email
                                binding.location.text = vendors.address
                                cat = vendors.category
                                imageList.addAll(vendors.images)
                                userImageAdapter = UserImageAdapter(imageList)
                                binding.imageViewPager.adapter = userImageAdapter
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