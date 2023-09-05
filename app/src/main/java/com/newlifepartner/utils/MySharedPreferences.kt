package com.newlifepartner.utils

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "MySharedPreferences"

        @Volatile
        private var instance: MySharedPreferences? = null

        fun getInstance(context: Context): MySharedPreferences =
            instance ?: synchronized(this) {
                instance ?: MySharedPreferences(context).also { instance = it }
            }
    }

    fun saveIntValue(key:String,value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun getIntValue(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun saveBooleanValue(key: String,value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBooleanValue(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun saveStringValue(key: String,value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringValue(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }
}
