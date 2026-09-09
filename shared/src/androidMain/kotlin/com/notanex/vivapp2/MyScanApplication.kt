package com.notanex.vivapp2

import android.app.Application
import qrgenerator.AppContext

class MyScanApplication : Application() {
    companion object {
        lateinit var INSTANCE: MyScanApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppContext.set(applicationContext)
    }
}