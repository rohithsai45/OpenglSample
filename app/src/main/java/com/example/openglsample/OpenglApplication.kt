package com.example.openglsample

import android.app.Application

class OpenglApplication : Application() {

    companion object{
        lateinit var instance : OpenglApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}