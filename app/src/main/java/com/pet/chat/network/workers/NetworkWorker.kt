package com.pet.chat.network.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pet.chat.helpers.*
import com.pet.chat.network.ConnectionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * For stable socket connection in future
 * */
@HiltWorker
class NetworkWorker @AssistedInject constructor(@Assisted context: Context, @Assisted workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            if (true/*!connectionManagerProvider.connectionManager.connectionActive()*/) {
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
      //  connectionManagerProvider.connectionManager.initConnection(uri, options)

    }

    private fun createHostUrl(): String {
        val networkHostTypeKey = inputData.getString(networkHostTypeKey)
        val hostBuilder = StringBuilder().append(networkHostTypeKey).append("://")
            .append(inputData.getString(networkIPKey))
        return hostBuilder.toString()
    }
}






