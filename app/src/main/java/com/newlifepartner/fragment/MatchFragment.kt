package com.newlifepartner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mohammedalaa.seekbar.DoubleValueSeekBarView
import com.mohammedalaa.seekbar.OnDoubleValueSeekBarChangeListener
import com.newlifepartner.MainActivity
import com.newlifepartner.adapter.CountrySpinnerAdapter
import com.newlifepartner.adapter.MatchFragmentAdapter
import com.newlifepartner.databinding.FragmentMatchBinding
import com.newlifepartner.databinding.SearchDialogBinding
import com.newlifepartner.modal.MatchProfile
import com.newlifepartner.modal.ResponseDashboard
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MatchFragment : Fragment() {

    private lateinit var binding:FragmentMatchBinding
    private val argument by navArgs<MatchFragmentArgs>()
    private var matchList: ArrayList<MatchProfile> = ArrayList()
    private lateinit var matchFragmentAdapter: MatchFragmentAdapter
    private var selectedCountry = ""
    private var type = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchBinding.inflate(layoutInflater)

        matchFragmentAdapter = MatchFragmentAdapter(matchList,findNavController())
        binding.matchRv.adapter = matchFragmentAdapter

        return binding.root
    }

    private fun callMatchUsersAPI(age: String, type: String, selectedCountry: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseDashboard<MatchProfile>> = safeApiCall { ApiService.retrofitService.getSearchUsers("0","100",type,age,selectedCountry) }
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

    private fun showSearchDialog() {
        val gender = arrayOf("Male", "Female", "Others")
        val builder = AlertDialog.Builder(requireContext())
        val binding = SearchDialogBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        builder.setCancelable(false)
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,gender)
        binding.genderSpinner.adapter = genderAdapter
        val cityList = (requireActivity() as MainActivity).cityList

        val cityAdapter = CountrySpinnerAdapter(requireContext(),cityList)
        binding.citySpinner.adapter = cityAdapter


        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCountry = cityList[position].id

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                type = gender[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.doubleRangeSeekbar.setOnRangeSeekBarViewChangeListener(object :
            OnDoubleValueSeekBarChangeListener {
            override fun onValueChanged(seekBar: DoubleValueSeekBarView?, min: Int, max: Int, fromUser: Boolean) {
                binding.ageMin.text = min.toString()
                binding.ageMax.text = max.toString()
            }

            override fun onStartTrackingTouch(seekBar: DoubleValueSeekBarView?, min: Int, max: Int) {
            }

            override fun onStopTrackingTouch(seekBar: DoubleValueSeekBarView?, min: Int, max: Int) {
            }
        })

        binding.btnSearch.setOnClickListener {
            val age = "${binding.ageMin.text} - ${binding.ageMax.text}"

            if (age.equals("0 - 0",true) || type.isEmpty() || selectedCountry.isEmpty()){
                Toast.makeText(requireContext(), "Please fill all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                callMatchUsersAPI(age,type,selectedCountry)
            }else{
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }

        }

        dialog.show()

    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setBackIcon()
    }

    override fun onStart() {
        super.onStart()
        if (argument.showDialog){
            showSearchDialog()
        }else{
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                callMatchUsersAPI(argument.age,argument.gender,argument.city)
            }else{
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

    }

}