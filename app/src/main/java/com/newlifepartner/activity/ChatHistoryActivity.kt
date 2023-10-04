package com.newlifepartner.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.newlifepartner.adapter.ChatHistoryAdapter
import com.newlifepartner.databinding.ActivityChatHistoryBinding
import com.newlifepartner.modal.ChatHistoryModal
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

class ChatHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatHistoryBinding
    private var userId = "0"
    private lateinit var chatHistoryAdapter: ChatHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = MySharedPreferences.getInstance(this).getStringValue(Constant.USER_ID)
        chatHistoryAdapter = ChatHistoryAdapter(ArrayList())
        binding.chatHistoryRv.adapter = chatHistoryAdapter

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
            val response : ResultType<ChatHistoryModal> = safeApiCall { ApiService.retrofitService.chatHistory(userId,"accept") }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            val list  = response.value.data as ArrayList<ChatHistoryModal.Data>
                            if (list.isEmpty()){
                                binding.chatHistoryRv.visibility = View.GONE
                                binding.noDataFound.visibility = View.VISIBLE
                            }else{
                                binding.chatHistoryRv.visibility = View.VISIBLE
                                binding.noDataFound.visibility = View.GONE
                            }
                            chatHistoryAdapter.updateList(list)
                        }else{
                            binding.chatHistoryRv.visibility = View.GONE
                            binding.noDataFound.visibility = View.VISIBLE
                        }
                    }
                    is ResultType.Error -> {
                        Toast.makeText(this@ChatHistoryActivity, response.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}