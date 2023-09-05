package com.newlifepartner.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.newlifepartner.MainActivity
import com.newlifepartner.R
import com.newlifepartner.activity.LoginActivity
import com.newlifepartner.activity.SignUpActivity
import com.newlifepartner.adapter.OnBoardingAdapter
import com.newlifepartner.databinding.ActivityOnBoardingBinding
import com.newlifepartner.modal.OnBoardingItem
import com.newlifepartner.utils.Constant
import com.newlifepartner.utils.MySharedPreferences

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private var onBoardingAdapter: OnBoardingAdapter? = null
    private lateinit var preferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        preferences = MySharedPreferences.getInstance(this)
        if (preferences.getBooleanValue(Constant.KEY_LOGIN)){
            startActivity(Intent(this,MainActivity::class.java))
        }else {
            setContentView(binding.root)
            initView()
        }

    }

    private fun initView() {
        setOnBoardingItem()
        binding.onboardingViewPager.adapter = onBoardingAdapter
        setOnBoardingIndicator()
        setCurrentOnBoardingIndicators(0)

        binding.onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentOnBoardingIndicators(position)
            }
        })
        binding.btnNext.setOnClickListener {
            binding.onboardingViewPager.currentItem = binding.onboardingViewPager.currentItem + 1

        }

        binding.ivBack.setOnClickListener {
            binding.onboardingViewPager.currentItem = binding.onboardingViewPager.currentItem - 1
        }

        binding.btnJoin.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

    }

    private fun setOnBoardingIndicator() {
        val indicators: Array<ImageView?> =
            arrayOfNulls(onBoardingAdapter!!.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext, R.drawable.ic_onboarding_indicator_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            binding.layoutOnboardingIndicators.addView(indicators[i])
        }
    }

    private fun setOnBoardingItem() {
        val onBoardingItems: ArrayList<OnBoardingItem> = ArrayList()

        val onBoardingScreen1 = OnBoardingItem(
            title = getString(R.string.onboarding_title_1),
            description = getString(R.string.onboarding_description_1),
            image = R.drawable.online_dating_bro_1
        )
        onBoardingItems.add(onBoardingScreen1)

        val onBoardingScreen2 = OnBoardingItem(
            title = getString(R.string.onboarding_title_2),
            description = getString(R.string.onboarding_description_2),
            image = R.drawable.onboarding_2
        )
        onBoardingItems.add(onBoardingScreen2)

        val onBoardingScreen3 = OnBoardingItem(
            title = getString(R.string.onboarding_title_3),
            description = getString(R.string.onboarding_description_3),
            image = R.drawable.onboarding_3
        )
        onBoardingItems.add(onBoardingScreen3)

        onBoardingAdapter = OnBoardingAdapter(onBoardingItems)
    }

    private fun setCurrentOnBoardingIndicators(index: Int) {
        val childCount: Int = binding.layoutOnboardingIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutOnboardingIndicators.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.ic_onboarding_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.ic_onboarding_indicator_inactive
                    )
                )
            }
        }
        if ((onBoardingAdapter?.itemCount ?: 0) > 1 && index == (onBoardingAdapter?.itemCount
                ?: 0) - 1
        ) {
            binding.joinNow.visibility = View.VISIBLE
            binding.areYouVendor.visibility = View.VISIBLE
            binding.btnNext.visibility = View.GONE
        } else {
            binding.joinNow.visibility = View.GONE
            binding.areYouVendor.visibility = View.GONE
            binding.btnNext.visibility = View.VISIBLE
        }

        if (binding.onboardingViewPager.currentItem == 0) {
            binding.ivBack.visibility = View.GONE
        } else {
            binding.ivBack.visibility = View.VISIBLE
        }
    }

    private fun moveToHome() {

        val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}