package com.newlifepartner.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.newlifepartner.databinding.ActivityLoginBinding
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.CustomProgressBar
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var progressDialog:CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = CustomProgressBar(this)

        binding.btnContinue.setOnClickListener {
            val number = binding.phoneEdt.text.toString()
            if (NetworkUtils.isNetworkAvailable(this)) {
                callOTPSentAPI(number)
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
        if (mobileNo.isEmpty()){
            Toast.makeText(this, "Please enter mobile number first", Toast.LENGTH_SHORT).show()
            return
        }
        val number = "91$mobileNo"
        progressDialog.show()
        lifecycleScope.launch(Dispatchers.IO+ ExceptionHandlerCoroutine.handler) {
            val response : ResultType<ResponseSignUp> = safeApiCall { ApiService.retrofitService.otpSent(number) }
            runOnUiThread {
                progressDialog.dismiss()
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            Toast.makeText(this@LoginActivity, response.value.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity,OtpActivity::class.java).putExtra("number",number))
                        }else{
                            Toast.makeText(this@LoginActivity, response.value.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResultType.Error -> {
                        Toast.makeText(this@LoginActivity, response.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}