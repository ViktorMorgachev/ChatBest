package com.pet.lovefinder.network

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pet.lovefinder.R
import com.pet.lovefinder.helpers.*
import io.socket.client.IO
import io.socket.client.On.on
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Event {
    data class DeleteMessage(val messageID: String) : Event()
    data class MessageSend(
        val room_id: Number,
        val text: String,
        val attachment_id: Int?,
    ) : Event()

}

class NetworkWorker(context: Context, workerParams: WorkerParameters) :
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
        val options =
            IO.Options.builder().setPath("/").setTransports(arrayOf("websocket", "long polling")).build()
        ConnectionManager.initConnection(uri, options)

    }

    private fun createHostUrl(): String {
        val networkHostTypeKey = inputData.getString(networkHostTypeKey)
        val hostBuilder = StringBuilder().append(networkHostTypeKey).append("://")
            .append(inputData.getString(networkIPKey)).append(":")
        if (networkHostTypeKey == NetworkHostType.WS.name.lowercase()) {
            hostBuilder.append(networkWSsocket)
        } else {
            hostBuilder.append(networkHttpSocket)
        }
        return hostBuilder.toString()
    }
}






