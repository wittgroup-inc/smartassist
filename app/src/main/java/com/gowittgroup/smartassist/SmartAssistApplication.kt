package com.gowittgroup.smartassist

import android.app.Application

class SmartAssistApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
