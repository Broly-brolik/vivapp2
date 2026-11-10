package com.notanex.vivapp2.logic

/** Tiny persistent key/value store for app flags (SharedPreferences on Android, NSUserDefaults on iOS). */
expect object AppPreferences {
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun putBoolean(key: String, value: Boolean)
}

object PrefKeys {
    const val ONBOARDING_DONE = "onboarding_done"
}
