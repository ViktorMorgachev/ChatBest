package com.pet.chat

import android.app.Application
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.pet.chat.helpers.*
import com.pet.chat.network.NetworkWorker
import com.pet.chat.storage.Prefs
import com.pet.chat.storage.States
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        var prefs: Prefs? = null
        var states: States? = null
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("Application", "onCreate()")

        instance = this
        prefs = Prefs(applicationContext)
        states = States(applicationContext)

        val workBuilder =
            OneTimeWorkRequestBuilder<NetworkWorker>().addTag(networkWorkerTag).setInputData(
                workDataOf(networkHostTypeKey to NetworkHostType.WS.name.lowercase(),
                    networkIPKey to networkIP,
                    networkSocketKey to networkWSsocket)).build()
        val workManager = WorkManager.getInstance(this)
        if (!workManager.isWorkScheduled(networkWorkerTag)) {
            workManager.enqueue(workBuilder)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d("Application", "onLowMemory()")
    }
}