package com.notanex.vivapp2.logic

import platform.Foundation.NSUserDefaults

actual object AppPreferences {
    private val defaults get() = NSUserDefaults.standardUserDefaults

    actual fun getBoolean(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) == null) default else defaults.boolForKey(key)

    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }
}
