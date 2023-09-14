package com.newlifepartner.network

import com.newlifepartner.modal.ChatModal
import com.newlifepartner.modal.City
import com.newlifepartner.modal.Data
import com.newlifepartner.modal.DataX
import com.newlifepartner.modal.GetVendorDetailModal
import com.newlifepartner.modal.HistoryModal
import com.newlifepartner.modal.MatchProfile
import com.newlifepartner.modal.ResponseDashboard
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.modal.UserDetailModal
import com.newlifepartner.modal.UserRequest
import com.newlifepartner.modal.Users
import com.newlifepartner.modal.Vendor
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface APIInterface {

    @FormUrlEncoded
    @POST("User_Signup")
    suspend fun signup(@Field("name") name: String,@Field("mobile_no") mobile: String,@Field("email") email: String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("User_OTP_Sent")
    suspend fun otpSent(@Field("mobile_no") mobileNo: String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("User_OTP_Verify")
    suspend fun verifyOTP(@Field("mobile_no") mobileNo: String, @Field("otp") otp: String, @Field("fcm_token") fcmToken: String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("Banner")
    suspend fun getBanner(@Field("start") start: String, @Field("limit") limit: String): Response<ResponseDashboard<Data>>


    @GET("Get_Category")
    suspend fun getCategory(): Response<ResponseDashboard<DataX>>

    @GET("Get_City")
    suspend fun getCity(): Response<ResponseDashboard<City>>

    @FormUrlEncoded
    @POST("Get_User")
    suspend fun getVerifiedUser(@Field("start") start: String, @Field("limit") limit: String, @Field("current_user") id: String): Response<ResponseDashboard<Users>>

    @FormUrlEncoded
    @POST("Get_Vendor")
    suspend fun getVendor(@Field("start") start: String, @Field("limit") limit: String): Response<ResponseDashboard<Vendor>>

    @FormUrlEncoded
    @POST("Find_User_Match_Search")
    suspend fun getSearchUsers(@Field("start") start: String, @Field("limit") limit: String,
                               @Field("gender") gender: String, @Field("age") age: String, @Field("location") locationId: String): Response<ResponseDashboard<MatchProfile>>

    @FormUrlEncoded
    @POST("Find_Vendor_Match_Search")
    suspend fun getSearchVendor(@Field("start") start: String, @Field("limit") limit: String,
                               @Field("cat") cat: String, @Field("city") city: String): Response<ResponseDashboard<Vendor>>

    @FormUrlEncoded
    @POST("User_Details")
    suspend fun getUsersById(@Field("id") id: String): Response<UserDetailModal>

    @FormUrlEncoded
    @POST("Vendor_Details")
    suspend fun getVendorById(@Field("id") id: String): Response<GetVendorDetailModal>

    @FormUrlEncoded
    @POST("Send_User_Notifications")
    suspend fun sentToken(@Field("fcm_token") token: String,@Field("user_id") id: String,@Field("type") type : String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("User_Update")
    suspend fun updateUserDetail(@Field("id") id: String,@Field("name") Name: String, @Field("age") age: String,
                                 @Field("gender") gender: String, @Field("bio") bio: String,@Field("interest") interest: String, @Field("hobbies") hobbies: String,
                                 @Field("relationship_status") relationship: String, @Field("caste") cast: String, @Field("religion") religion: String, @Field("city") city: String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("Add_Vendor_Lead")
    suspend fun sendVendorLead(@Field("vendor_id") vendorId: String, @Field("name") name: String,
                                @Field("contact_number") contactNo: String, @Field("email") email: String,
                               @Field("function_date") functionDate: String,@Field("budget") budget: String,@Field("vendor_category") vendorCat: String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("Fetch_Vendor_Lead")
    suspend fun fetchVendorLead(@Field("vendor_lead_id") id: String): Response<HistoryModal>


    @FormUrlEncoded
    @POST("User_chats")
    suspend fun sendMessage(@Field("from_user") fromUser: String,@Field("to_user") toUser: String,@Field("message") message: String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("Fetch_Individual_Chats")
    suspend fun getMessage(@Field("from_user") fromUser: String,@Field("to_user") toUser: String): Response<ResponseDashboard<ChatModal>>

    @FormUrlEncoded
    @POST("User_Contact_Request")
    suspend fun connect(@Field("user_id") userId: String,@Field("request_to") requestedTo: String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("User_Request_Update")
    suspend fun updateRequest(@Field("user_id") userId: String,@Field("request_to") requestedTo: String,@Field("status") status: String): Response<ResponseSignUp>

    @FormUrlEncoded
    @POST("Get_Singleuser_Contact")
    suspend fun getUserContact(@Field("user_id") userId: String,@Field("request_to") requestTo: String): Response<UserRequest>

    @POST("User_Galler_Add")
    @FormUrlEncoded
    suspend fun uploadImage(@Field("user_id") userId:String, @Field("files") file: List<String>): Response<ResponseSignUp>

}