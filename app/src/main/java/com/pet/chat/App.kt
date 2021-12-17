package com.pet.chat

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.pet.chat.helpers.*
import com.pet.chat.network.NetworkWorker
import com.pet.chat.storage.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        var prefs: Prefs? = null
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()


        instance = this
        prefs = Prefs(applicationContext)

        val workBuilder = OneTimeWorkRequestBuilder<NetworkWorker>().addTag(networkWorkerTag).setInputData(
            workDataOf(networkHostTypeKey to NetworkHostType.WS.name.lowercase(), networkIPKey to networkIP, networkSocketKey to networkWSsocket)).build()
        val workManager = WorkManager.getInstance(this)
        if (!workManager.isWorkScheduled(networkWorkerTag)) {
            workManager.enqueue(workBuilder)
        }
    }
}