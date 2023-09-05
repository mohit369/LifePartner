package com.newlifepartner.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log

@SuppressLint("LongLogTag")
object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        // add this permission in AndroidManifest.xml
        //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        try {
            if (cm != null) {
                //if (Build.VERSION.SDK_INT < 23)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    val ni = cm.activeNetworkInfo
                    if (ni != null) {
                        return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                    }
                } else {
                    val n = cm.activeNetwork
                    if (n != null) {
                        val nc = cm.getNetworkCapabilities(n)
                        return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                            NetworkCapabilities.TRANSPORT_WIFI
                        ) || nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("isNetworkAvailable :", e.toString())
        }
        return false
    }
}