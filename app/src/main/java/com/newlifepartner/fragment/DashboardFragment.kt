package com.newlifepartner.fragment

import android.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.newlifepartner.MainActivity
import com.newlifepartner.adapter.CountrySpinnerAdapter
import com.newlifepartner.adapter.FindWeddingVendorAdapter
import com.newlifepartner.adapter.SliderAdapter
import com.newlifepartner.adapter.TrustedVendorAdapter
import com.newlifepartner.adapter.VerifiedMatesAdapter
import com.newlifepartner.databinding.CitySearchDialogBinding
import com.newlifepartner.databinding.FragmentDashboardBinding
import com.newlifepartner.databinding.SearchDialogBinding
import com.newlifepartner.modal.City
import com.newlifepartner.modal.Data
import com.newlifepartner.modal.DataX
import com.newlifepartner.modal.ResponseDashboard
import com.newlifepartner.modal.Users
import com.newlifepartner.modal.Vendor
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.Constant
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.MySharedPreferences
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Data>
    private lateinit var findWeddingVendorList: ArrayList<DataX>
    private lateinit var verifiedMatesList: ArrayList<Users>
    private lateinit var adapter: SliderAdapter
    private lateinit var weddingVendorAdapter: FindWeddingVendorAdapter
    private lateinit var verifiedMatesAdapter: VerifiedMatesAdapter
    private lateinit var trustedVendorAdapter: TrustedVendorAdapter
    private var selectedCountry = ""
    private var selectedCountryVendor = ""
    private var age = ""
    private var type = ""
    private var cityList:ArrayList<City> = ArrayList()
    var showDialog = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater)

        init()
        setUpTransformer()

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2000)
            }
        })

        weddingVendorAdapter = FindWeddingVendorAdapter(object : FindWeddingVendorAdapter.VendorClick{
            override fun vendorClick(id: String) {
                showVendorCityDialog(id)
            }

        })

        binding.searchEdt.setOnClickListener {
            showSearchDialog()
        }

        binding.seeMoreVerified.setOnClickListener {
            val action = DashboardFragmentDirections.actionActionHomeToActionMatch("0","","",showDialog)
            findNavController().navigate(action)
        }

        binding.seeMoreTrusted.setOnClickListener {
            val action = DashboardFragmentDirections.actionActionHomeToActionVendor("","",showDialog)
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun showSearchDialog() {
        val gender = arrayOf("Male", "Female", "Others")
        val builder = AlertDialog.Builder(requireContext())
        val binding = SearchDialogBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        val genderAdapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1,gender)
        binding.genderSpinner.adapter = genderAdapter

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

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current SeekBar progress
                binding.age.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Handle the start of SeekBar tracking (e.g., save initial value)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Handle the end of SeekBar tracking (e.g., use the final value)
            }
        })

        binding.btnSearch.setOnClickListener {
            val age = binding.age.text.toString()

            if (age.equals("0",true) || type.isEmpty() || selectedCountry.isEmpty()){
                Toast.makeText(requireContext(), "Please fill all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            showDialog = false
            val action = DashboardFragmentDirections.actionActionHomeToActionMatch(age,type,selectedCountry,showDialog)
            findNavController().navigate(action)

        }

        dialog.show()

    }


    private fun showVendorCityDialog(id: String) {
        val builder = AlertDialog.Builder(requireContext())
        val binding = CitySearchDialogBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()

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
            showDialog = false
            val action = DashboardFragmentDirections.actionActionHomeToActionVendor(selectedCountryVendor,id,showDialog)
            findNavController().navigate(action)

        }

        dialog.show()

    }

    private val runnable = Runnable {
        binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1
    }

    private fun init() {
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            callDashBoardAPIs()
            callGetCityAPI()
        }else{
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }

    }

    private fun callGetCityAPI() {
        cityList.clear()
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseDashboard<City>> = safeApiCall { ApiService.retrofitService.getCity() }
            withContext(Dispatchers.Main) {
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            cityList.addAll(response.value.data)
                            cityList.sortBy { it.city }
                            (requireActivity() as MainActivity).cityList = cityList
                        }
                    }
                    is ResultType.Error -> {
                    }
                }
            }
        }
    }

    private fun callDashBoardAPIs() {
        binding.dashbordLayout.visibility = View.GONE
        CoroutineScope(Dispatchers.IO + ExceptionHandlerCoroutine.handler).launch {
            supervisorScope {
                val bannerAPI = async { callBannerAPI() }
                val category = async { callGetCategory() }
                val getUsers = async { callGetUsersAPI() }
                val getVendor = async { callGetVendor() }

                bannerAPI.await()
                val listCategory = category.await()
                val verifiedList = getUsers.await()
                val vendorList = getVendor.await()
                withContext(Dispatchers.Main){
                    binding.progressBar.visibility = View.GONE
                    binding.dashbordLayout.visibility = View.VISIBLE
                    binding.findWeddingRv.adapter = weddingVendorAdapter
                    weddingVendorAdapter.updateList(listCategory)
                    verifiedMatesAdapter = VerifiedMatesAdapter(verifiedList,findNavController())
                    binding.verifiedMatesRv.adapter = verifiedMatesAdapter
                    trustedVendorAdapter = TrustedVendorAdapter(vendorList,findNavController())
                    binding.trustedVendorRv.adapter = trustedVendorAdapter

                }

            }
        }
    }

    private suspend fun callGetVendor():ArrayList<Vendor> {
        var vendorList:ArrayList<Vendor> = ArrayList()
        val response: ResultType<ResponseDashboard<Vendor>> = safeApiCall { ApiService.retrofitService.getVendor("0","10") }
        when (response) {
            is ResultType.Success -> {
                if (response.value.status) {
                    vendorList =  response.value.data as ArrayList<Vendor>
                }
            }
            is ResultType.Error -> {
                return ArrayList()
            }
        }
        return vendorList
    }

    private suspend fun callGetUsersAPI():ArrayList<Users> {
        val id = MySharedPreferences.getInstance(requireContext()).getStringValue(Constant.USER_ID)
        val response: ResultType<ResponseDashboard<Users>> = safeApiCall { ApiService.retrofitService.getVerifiedUser("0","10",id) }
        when (response) {
            is ResultType.Success -> {
                if (response.value.status) {
                    verifiedMatesList = response.value.data as ArrayList<Users>
                }
            }
            is ResultType.Error -> {
                return ArrayList()
            }
        }
        return verifiedMatesList
    }

    private suspend fun callGetCategory():ArrayList<DataX> {
        val response: ResultType<ResponseDashboard<DataX>> =
            safeApiCall { ApiService.retrofitService.getCategory() }
        when (response) {
            is ResultType.Success -> {
                if (response.value.status) {
                    findWeddingVendorList = response.value.data as ArrayList<DataX>

                }
            }
            is ResultType.Error -> {
               return ArrayList()
            }
        }
        return findWeddingVendorList
    }

    private suspend fun callBannerAPI() {
        val response: ResultType<ResponseDashboard<Data>> =
            safeApiCall { ApiService.retrofitService.getBanner("0", "10") }
        when (response) {
            is ResultType.Success -> {
                if (response.value.status) {

                    imageList = response.value.data as ArrayList<Data>
                    adapter = SliderAdapter(imageList, binding.viewPager2)

                    binding.viewPager2.adapter = adapter
                    binding.viewPager2.offscreenPageLimit = 3
                    binding.viewPager2.clipToPadding = false
                    binding.viewPager2.clipChildren = false
                    binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

                }
            }

            is ResultType.Error -> {
            }
        }
    }


    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        binding.viewPager2.setPageTransformer(transformer)
    }

    override fun onResume() {
        super.onResume()
        showDialog = true
        (requireActivity() as MainActivity).setHomeIcon()
    }


}