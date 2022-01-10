package com.pet.chat

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.pet.chat.helpers.*
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.data.DataNetworkProvider
import com.pet.chat.network.workers.NetworkWorker
import com.pet.chat.storage.Prefs
import com.pet.chat.storage.States
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    companion object {
        var prefs: Prefs? = null
        var states: States? = null
        lateinit var instance: App
            private set
    }
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)


    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var dataNetworkProvider: DataNetworkProvider
    @Inject lateinit var connectionManager: ConnectionManager

    override fun onCreate() {
        super.onCreate()

        Log.d("Application", "onCreate()")

        instance = this
        prefs = Prefs(applicationContext)
        states = States(applicationContext)

        dataNetworkProvider.observe(applicationScope)
        //startWorker()
        connectionManager.initConnection()
    }

    private fun startWorker() {
        val workBuilder = OneTimeWorkRequestBuilder<NetworkWorker>().addTag(socketConnectionWorkerTag).apply {
                setInputData(workDataOf(networkHostTypeKey to NetworkHostType.WS.name.lowercase(),
                        networkIPKey to networkIP,
                        networkSocketKey to networkWSsocket))
            }.build()
        val workManager = WorkManager.getInstance(this)
        if (!workManager.isWorkScheduled(socketConnectionWorkerTag)) {
            workManager.enqueue(workBuilder)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d("Application", "onLowMemory()")
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}