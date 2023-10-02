package com.newlifepartner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.lifecycle.lifecycleScope
import com.newlifepartner.adapter.NotificationAdapter
import com.newlifepartner.databinding.ActivityNotificationBinding
import com.newlifepartner.modal.NotificationModel
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

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding:ActivityNotificationBinding
    private var userId = "0"
    private lateinit var notificationAdapter: NotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = MySharedPreferences.getInstance(this).getStringValue(Constant.USER_ID)
        notificationAdapter = NotificationAdapter(ArrayList())
        binding.notificationRv.adapter = notificationAdapter

        binding.icBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (NetworkUtils.isNetworkAvailable(this)) {
            callNotificationAPI()
        }else{
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }


    private fun callNotificationAPI() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<NotificationModel> = safeApiCall { ApiService.retrofitService.getNotification(userId,"user") }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            val list  = response.value.data as ArrayList<NotificationModel.Data>
                            if (list.isEmpty()){
                                binding.notificationRv.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            }else{
                                binding.notificationRv.visibility = View.VISIBLE
                                binding.noDataFound.visibility = View.GONE
                            }
                            notificationAdapter.updateList(list)
                        }else{
                            binding.notificationRv.visibility = View.GONE
                            binding.noDataFound.visibility = View.VISIBLE
                        }
                    }
                    is ResultType.Error -> {
                        Toast.makeText(this@NotificationActivity, response.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}