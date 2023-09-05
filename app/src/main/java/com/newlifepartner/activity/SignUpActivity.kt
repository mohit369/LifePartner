package com.newlifepartner.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.newlifepartner.databinding.ActivitySignUpBinding
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.CustomProgressBar
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.NetworkUtils
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var progressDialog: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClickListener()
        progressDialog = CustomProgressBar(this)


    }

    private fun initClickListener(){

        binding.btnRegister.setOnClickListener {
            val name = binding.nameEdt.text.toString()
            val email = binding.emailEdt.text.toString().trim()
            val mobile = "91${binding.mobileEdt.text}"

            if (name.isEmpty() || email.isEmpty() || mobile.isEmpty()){
                Toast.makeText(this, "Please fil all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{

                val patern = Pattern.matches(emailPattern,email)
                if (patern){
                    if (binding.termsAndConditionCb.isChecked) {
                        if (NetworkUtils.isNetworkAvailable(this)) {
                            progressDialog.show()
                            callSignUp(name, email, mobile)
                        }else{
                            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "Please check terms and condition", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.alreadyAnAccount.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
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


    private fun callSignUp(name: String, email: String, mobile: String) {
        lifecycleScope.launch(Dispatchers.IO+ExceptionHandlerCoroutine.handler) {
           val response : ResultType<ResponseSignUp> = safeApiCall { ApiService.retrofitService.signup(name, mobile, email) }
            runOnUiThread {
                progressDialog.dismiss()
                when (response) {
                    is ResultType.Success -> {
                        if (response.value.status){
                            Toast.makeText(this@SignUpActivity, response.value.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
                            finish()
                            finishAffinity()
                        }else{
                            Toast.makeText(this@SignUpActivity, response.value.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResultType.Error -> {
                        Toast.makeText(this@SignUpActivity, response.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}