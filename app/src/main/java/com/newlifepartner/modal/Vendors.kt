package com.newlifepartner.modal


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Vendors(
    @SerializedName("about_bus")
    val aboutBus: String,
    @SerializedName("address")
    val address: String?,
    @SerializedName("category")
    val category: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("company_name")
    val companyName: String,
    @SerializedName("contact_no")
    val contactNo: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("images")
    val images: List<Image>,
    @SerializedName("mobile_for_leads")
    val mobileForLeads: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("package")
    val packageX: String,
    @SerializedName("starting_budget")
    val startingBudget: String,
    @SerializedName("website")
    val website: String
)