package com.pet.chat

import android.app.Application
import android.util.Log
import com.pet.chat.ui.MainChatModule
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var  mainChatModule: MainChatModule

    override fun onCreate() {
        super.onCreate()
        mainChatModule.initialize(applicationContext)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d("Application", "onLowMemory()")
    }
}