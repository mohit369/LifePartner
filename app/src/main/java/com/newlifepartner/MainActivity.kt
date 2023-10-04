package com.newlifepartner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.newlifepartner.activity.ChatActivity
import com.newlifepartner.activity.ChatHistoryActivity
import com.newlifepartner.activity.NotificationActivity
import com.newlifepartner.databinding.ActivityMainBinding
import com.newlifepartner.modal.City
import com.newlifepartner.modal.ResponseSignUp
import com.newlifepartner.network.ApiService
import com.newlifepartner.utils.Constant
import com.newlifepartner.utils.ExceptionHandlerCoroutine
import com.newlifepartner.utils.MySharedPreferences
import com.newlifepartner.utils.ResultType
import com.newlifepartner.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding
    private var isBack = false
    var cityList:ArrayList<City> = ArrayList()
    private lateinit var navController:NavController
    private lateinit var preferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavView
        navController = findNavController(R.id.container)
        navView.setupWithNavController(navController)
        preferences = MySharedPreferences.getInstance(this)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPermissionGranted()) {
            requestNotificationPermission()
        }
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    navController.popBackStack(item.itemId, inclusive = false)
                }
                R.id.action_match -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    navController.popBackStack(item.itemId, inclusive = false)
                }
                R.id.action_profile -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    navController.popBackStack(item.itemId, inclusive = false)
                }
                R.id.action_vendor -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    navController.popBackStack(item.itemId, inclusive = false)
                }
            }
            true
        }

        binding.navView.setNavigationItemSelectedListener(this)

        binding.homeIcon.setOnClickListener {
            if (isBack){
                navController.popBackStack()
            }else{
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        binding.notification.setOnClickListener {
            startActivity(Intent(this,NotificationActivity::class.java))
        }

        binding.chat.setOnClickListener {
            startActivity(Intent(this,ChatHistoryActivity::class.java))
        }


    }

    private fun isNotificationPermissionGranted(): Boolean {
          return  ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }



    private fun requestNotificationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {

            } else {
              showPermissionExplanationOrNavigateToSettings()
            }
        }

    private fun showPermissionExplanationOrNavigateToSettings() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("This permission is needed to sent important notification.")
                .setPositiveButton("Grant Permission") { _, _ ->
                   requestNotificationPermission()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            val settingsIntent = Intent().apply {
                action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(settingsIntent)
        }
    }


    fun setHomeIcon(){
        isBack = false
        binding.chat.visibility = View.VISIBLE
        binding.notification.visibility = View.VISIBLE
        binding.homeIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.menu_icon,null))
    }

    fun setBackIcon(){
        isBack = true
        binding.location.visibility = View.GONE
        binding.chat.visibility = View.GONE
        binding.notification.visibility = View.GONE
        binding.homeIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_back_black,null))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> {
                navController.setGraph(R.navigation.home_navigations)
            }
            R.id.about -> {
                openBrowser("http://matri.compuhost.co.in/about_us")
            }
            R.id.contact -> {
                openBrowser("http://matri.compuhost.co.in/contact_us")
            }
            R.id.terms -> {
                openBrowser("http://matri.compuhost.co.in/term_condition")
            }
            R.id.privacy -> {
                openBrowser("http://matri.compuhost.co.in/privacy_policy")
            }
            R.id.disclaimer -> {
                openBrowser("http://matri.compuhost.co.in/disclaimer")
            }
            R.id.refund -> {
                openBrowser("https://www.google.com/")
            }
            R.id.history -> {
                navController.navigate(R.id.history)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    private fun openBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onBackPressed() {
        if (!isBack){
            finish()
            finishAffinity()
        }else{
            super.onBackPressed()
        }
    }

}