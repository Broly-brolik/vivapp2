package com.notanex.vivapp2.logic

import android.content.Context
import com.notanex.vivapp2.MyScanApplication

actual object AppPreferences {
    private val prefs by lazy {
        MyScanApplication.INSTANCE.getSharedPreferences("vivapp_prefs", Context.MODE_PRIVATE)
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}
