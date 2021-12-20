package com.pet.chat.network.workers

import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.pet.chat.App
import com.pet.chat.events.InternalEvent
import com.pet.chat.events.InternalEventsProvider
import com.pet.chat.network.data.responce.LoadFileResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.lang.StringBuilder
import javax.inject.Inject
import com.pet.chat.network.data.send.MessageWithFile
import com.pet.chat.network.data.send.File as SendFile

const val roomID = "ROOM_ID"
const val type = "FILE_TYPE"
const val filePath = "FILE_PATH"
const val messageID = "MESSAGE_ID"
const val text = "TEXT"

fun Data.toFile(): MessageWithFile {
    return MessageWithFile(
        file = SendFile(
            roomID = this.getInt(roomID, -1),
            type = this.getString(type) ?: "",
            filePath = this.getString(filePath) ?: "",
        ),
        messageID = this.getInt(messageID, -1),
        text = this.getString(text) ?: ""
    )
}

@HiltWorker
class FileUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private val client = OkHttpClient().newBuilder().addInterceptor(HttpLoggingInterceptor())
    @Inject lateinit var internalEventsProvider: InternalEventsProvider

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val result = uploadFile(inputData.toFile().file)
            // мы не должны напрямую взаимодействовать в viewModel
            //internalEventsProvider.internalEvents.emit(InternalEvent.FileSuccessDownload(inputData.toFile().messageID))
            Result.success()
        } catch (error: Throwable) {
            internalEventsProvider.internalEvents.emit(InternalEvent.FileErrorUpload(inputData.toFile().messageID))
            error.printStackTrace()
            Result.failure()
        }
    }

    suspend private fun uploadFile(file: SendFile) {

        //http://185.26.121.63:3000/upload?token=abcde&type=video&user_id=123
        val baseUrl = "http://185.26.121.63:3001/upload?"

        val fileToUpload = File(file.filePath)
        val fileUri = FileProvider.getUriForFile(applicationContext,
            applicationContext.packageName + ".fileprovider",
            fileToUpload)
        val type = applicationContext.contentResolver.getType(fileUri)
        val requestFile: RequestBody = RequestBody.create(MediaType.parse(type!!), fileToUpload)
        val filePart = MultipartBody.Part.createFormData("picture", fileToUpload.name, requestFile)

        val requestBuilder = StringBuilder()
        requestBuilder
            .append("token=${App.prefs!!.userToken}&")
            .append("type=${file.type}&")
            .append("user_id=${App.prefs!!.userID}&")
            .append("room_id=${file.roomID.toInt()}")

        val request = Request.Builder()
            .url(baseUrl.plus(requestBuilder))
            .post(requestFile)
            .build()

        val response = client.build().newCall(request).execute()
        if (response.isSuccessful) {
            Log.d("FileUploadWorker", "Responce sucess Responce data: ${response.body()}")
        } else {
            Log.d("FileUploadWorker", "Responce Error: ${response.body()} : ${response.code()} Messages ${response.message()}")
            internalEventsProvider.internalEvents.emit(InternalEvent.FileErrorUpload(inputData.toFile().messageID))
        }
    }
}