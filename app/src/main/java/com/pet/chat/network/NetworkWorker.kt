package com.pet.chat.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pet.chat.helpers.*
import io.socket.client.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NetworkWorker @Inject constructor(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            if (!ConnectionManager.connectionActive()) {
                initializeConnection()
            }
            Result.success()
        } catch (error: Throwable) {
            error.printStackTrace()
            Result.retry()
        }
    }

    private fun initializeConnection() {
        val uri = createHostUrl()
        val port = if (networkHostTypeKey == NetworkHostType.WS.name.lowercase()) {
            networkWSsocket
        } else {
            networkHttpSocket
        }
        val options = IO.Options.builder().setPath("/").setTransports(arrayOf("websocket", "polling"))
                .setPort(port.toInt()).build()
        ConnectionManager.initConnection(uri, options)

    }

    private fun createHostUrl(): String {
        val networkHostTypeKey = inputData.getString(networkHostTypeKey)
        val hostBuilder = StringBuilder().append(networkHostTypeKey).append("://")
            .append(inputData.getString(networkIPKey))
        return hostBuilder.toString()
    }
}





