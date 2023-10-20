package com.newlifepartner.activity

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.newlifepartner.adapter.ChatAdapter
import com.newlifepartner.databinding.ActivityChatBinding
import com.newlifepartner.modal.ChatModal
import com.newlifepartner.modal.ResponseDashboard
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.Constant
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.MySharedPreferences
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatRoomAdapter: ChatAdapter
    private lateinit var chatList: ArrayList<ChatModal>
    private lateinit var fromId:String
    private lateinit var usename:String
    private var socket:Socket? = null
    private var toId = ""
    private val myReceiver = MyReceiver()
    val gson: Gson = Gson()
    private lateinit var notificationManager : NotificationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toId = intent.getStringExtra("UserID")!!
        usename = intent.getStringExtra("name")!!
        fromId = MySharedPreferences.getInstance(this).getStringValue(Constant.USER_ID)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        chatList = ArrayList()
        chatRoomAdapter = ChatAdapter(this,chatList)
        binding.messageRv.adapter = chatRoomAdapter
        binding.username.text = usename
        getMessage()

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        try {
            //This address is the way you can connect to localhost with AVD(Android Virtual Device)
            socket = IO.socket("http://10.0.2.2:3000")
            socket?.id()?.let { Log.d("success", it) }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }

        socket?.apply {
            connect()
            //Register all the listener and callbacks here.
            on(Socket.EVENT_CONNECT, onConnect)
            on("newUserToChatRoom", onNewUser) // To know if the new user entered the room.
            on("updateChat", onUpdateChat) // To update if someone send a message to chatroom
            on("userLeftChatRoom", onUserLeft)
        }

    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "Message_received_chat") {
                notificationManager.cancelAll()
                getMessage()
            }
        }
    }


    private var onConnect = Emitter.Listener {
        Log.d("TAG", ": Socket connected")
        //After getting a Socket.EVENT_CONNECT which indicate socket has been connected to server,
        //send userName and roomName so that they can join the room.
       // val data = initialData(userName, roomName)
        //val jsonData = gson.toJson(data) // Gson changes data object to Json type.
       // mSocket.emit("subscribe", jsonData)
    }

    private var onNewUser = Emitter.Listener {
       /* val name = it[0] as String //This pass the userName!
        val chat = Message(name, "", roomName, MessageType.USER_JOIN.index)
        addItemToRecyclerView(chat)
        Log.d(TAG, "on New User triggered.")*/
    }

    private var onUserLeft = Emitter.Listener {
        /*val leftUserName = it[0] as String
        val chat: Message = Message(leftUserName, "", "", MessageType.USER_LEAVE.index)
        addItemToRecyclerView(chat)*/
    }

    private var onUpdateChat = Emitter.Listener {
        val chat: ChatModal = gson.fromJson(it[0].toString(), ChatModal::class.java)
        //chat.isSent = false
        addItemToRecyclerView(chat)
    }

    private fun sendMessage() {
        val message = binding.msgBox.text.toString().trim()
        if (message.isEmpty()){
            return
        }
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseSignUp> = safeApiCall { ApiService.retrofitService.sendMessage(fromId,toId,message) }
            withContext(Dispatchers.Main) {
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            getMessage()
                        }
                    }
                    is ResultType.Error -> {

                    }
                }
            }
        }
    }

    fun getMessage() {
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseDashboard<ChatModal>> = safeApiCall { ApiService.retrofitService.getMessage(fromId,toId) }
            withContext(Dispatchers.Main) {
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            binding.msgBox.setText("")
                            chatRoomAdapter.updateList(response.value.data as ArrayList<ChatModal>)
                            binding.messageRv.smoothScrollToPosition(response.value.data.size - 1)
                        }
                    }
                    is ResultType.Error -> {

                    }
                }
            }
        }
    }

    private fun addItemToRecyclerView(message: ChatModal) {

        //Since this function is inside of the listener,
        //You need to do it on UIThread!
        runOnUiThread {
            chatList.add(message)
            chatRoomAdapter.notifyItemInserted(chatList.size)
            binding.msgBox.setText("")
            binding.messageRv.smoothScrollToPosition(chatList.size - 1) //move focus on last message
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(myReceiver, IntentFilter("Message_received_chat"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myReceiver)
    }
    override fun onDestroy() {
        super.onDestroy()
        socket?.let {
            it.disconnect()
        }
    }
}