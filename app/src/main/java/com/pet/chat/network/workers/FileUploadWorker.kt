package com.pet.chat.network.workers

import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.pet.chat.App
import com.pet.chat.providers.InternalEvent
import com.pet.chat.providers.InternalEventsProvider
import com.pet.chat.ui.MainChatModule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.lang.StringBuilder

// Don't use this, bad practice, but code exellent :)
object DataBuffer {
    private var data: List<Any> = listOf()

    fun write(data: Any) {
        synchronized(this) {
            this.data = listOf(data)
        }
    }

    fun read(): Any? {
        synchronized(this) {
            val dataForResult = data.firstOrNull()
            data = listOf()
            return dataForResult
        }
    }
}

class FileUploadConverter {

    companion object {
        const val roomID = "ROOM_ID"
        const val filePath = "FILE_PATH"
        const val fileID = "FILE_ID"
        const val state = "STATE"
        const val fileType = "FILE_TYPE"
        const val message = "MESSAGE"
    }

    fun dataToSendingFile(data: Data): SendingFile {
        return SendingFile(
            filePath = data.getString(filePath)!!,
            roomID = data.getInt(roomID, -1),
            fileType = data.getString(fileType)!!,
            message = data.getString(message) ?: "",
            fileName = "sending_file"
        )
    }


}

data class SendingFile(
    val filePath: String,
    val message: String,
    val fileName: String,
    val fileType: String,
    val roomID: Int,
)

@HiltWorker
class FileUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val eventsProvider: InternalEventsProvider,
) : CoroutineWorker(context, workerParams) {

    private val client = OkHttpClient().newBuilder().addInterceptor(HttpLoggingInterceptor())
   private var data: SendingFile? = null
    
    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val fileUploadConverter = FileUploadConverter()
            data = fileUploadConverter.dataToSendingFile(inputData)
            val result = uploadFile(data!!)
            eventsProvider.internalEvents.emit(InternalEvent.LoadingFileLoading(data!!))
            Result.success()
        } catch (error: Throwable) {
            eventsProvider.internalEvents.emit(InternalEvent.LoadingFileError(data!!))
            error.printStackTrace()
            Result.failure()
        }
    }

    suspend private fun uploadFile(file: SendingFile) {

        val baseUrl = "http://185.26.121.63:3001/upload/?"

        val fileToUpload = File(file.filePath)
        val fileUri = FileProvider.getUriForFile(applicationContext,
            applicationContext.packageName + ".fileprovider",
            fileToUpload)
        val type = applicationContext.contentResolver.getType(fileUri)
        val requestFile: RequestBody = RequestBody.create(MediaType.parse(type!!), fileToUpload)
        val filePart = MultipartBody.Part.createFormData("picture", fileToUpload.name, requestFile)

        val requestBuilder = StringBuilder()
        requestBuilder
            .append("token=${MainChatModule.chatsPrefs?.userToken}&")
            .append("type=${file.fileType}&")
            .append("user_id=${MainChatModule.chatsPrefs?.userID}&")
            .append("room_id=${file.roomID.toInt()}")

        val request = Request.Builder()
            .url(baseUrl.plus(requestBuilder))
            .post(requestFile)
            .build()

        Log.d("FileUploadWorker",
            "UploadFile: URL ${baseUrl.plus(requestBuilder)} File ${fileToUpload.canonicalPath}")

        val response = client.build().newCall(request).execute()
        if (response.isSuccessful) {

            Log.d("FileUploadWorker", "Response success Responce data: ${response.body()}")
        } else {
            eventsProvider.internalEvents.emit(InternalEvent.LoadingFileError(data!!))
            Log.d("FileUploadWorker",
                "Responce Error: ${response.body()} : ${response.code()} Messages ${response.message()}")
            //InternalEventsProvider.internalEvents.emit(InternalEvent.FileErrorUpload(inputData.toFile().messageID))
        }
    }
}