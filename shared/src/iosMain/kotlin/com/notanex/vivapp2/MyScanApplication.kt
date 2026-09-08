package com.notanex.vivapp2

import platform.UIKit.Application
import ...AppContext

class MyScanApplication : Application() {
    companion object { lateinit var INSTANCE: MyScanApplication }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppContext.apply { set(applicationContext) }
    }
}