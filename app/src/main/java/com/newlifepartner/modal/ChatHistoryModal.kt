package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName

data class ChatHistoryModal(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
) {
    data class Data(
        @SerializedName("date_time")
        val dateTime: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("image")
        val image: String,
        @SerializedName("last_message")
        val lastMessage: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("request_to")
        val requestTo: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("user_id")
        val userId: String
    )
}