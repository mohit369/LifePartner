package com.newlifepartner.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.newlifepartner.MainActivity
import com.newlifepartner.R
import com.newlifepartner.databinding.ActivityOtpBinding
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.Constant
import com.newlifepartner.utils.CustomProgressBar
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.MySharedPreferences
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private var number:String? = null
    private lateinit var progressDialog: CustomProgressBar
    private lateinit var prefrences:MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        number = intent.getStringExtra("number")
        progressDialog = CustomProgressBar(this)
        prefrences = MySharedPreferences.getInstance(this)

        binding.btnVerify.setOnClickListener {

            val otp = binding.otpView.otp
            if (otp != null && otp.length > 3) {
                if (otp.isEmpty()){
                    Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
                }else{
                    if (NetworkUtils.isNetworkAvailable(this)) {
                        number?.let { verifyOtp(it, otp) }
                    }else{
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        binding.dontReceiveOtp.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                number?.let { callOTPSentAPI(it) }
            }else{
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.icBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun callOTPSentAPI(mobileNo : String) {
        progressDialog.show()
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseSignUp> = safeApiCall { ApiService.retrofitService.otpSent("$91$mobileNo") }
            runOnUiThread {
                progressDialog.dismiss()
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            Toast.makeText(this@OtpActivity, response.value.message, Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@OtpActivity, response.value.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResultType.Error -> {
                        Toast.makeText(this@OtpActivity, response.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun verifyOtp(mobileNo : String, otp :String) {
        progressDialog.show()
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseSignUp> = safeApiCall { ApiService.retrofitService.verifyOTP(mobileNo,otp) }
            runOnUiThread {
                progressDialog.dismiss()
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            Toast.makeText(this@OtpActivity, response.value.message, Toast.LENGTH_SHORT).show()
                            prefrences.saveBooleanValue(Constant.KEY_LOGIN,true)
                            response.value.userId?.let {
                                prefrences.saveStringValue(Constant.USER_ID,response.value.userId)
                            }
                            startActivity(Intent(this@OtpActivity,MainActivity::class.java))
                        }else{
                            Toast.makeText(this@OtpActivity, response.value.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResultType.Error -> {
                        Toast.makeText(this@OtpActivity, response.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}