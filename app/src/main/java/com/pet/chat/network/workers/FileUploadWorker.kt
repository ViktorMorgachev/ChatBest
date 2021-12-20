package com.pet.chat.network.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.pet.chat.App
import com.pet.chat.network.data.responce.UploadFileResponse
import com.pet.chat.network.services.UploadFileService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import javax.inject.Inject
import com.pet.chat.network.data.send.File as SendFile

const val roomID = "ROOM_ID"
const val type = "FILE_TYPE"
const val filePath = "FILE_PATH"

fun Data.toFile(): SendFile {
    return SendFile(
        room = this.getInt(roomID, -1),
        type = this.getString(type) ?: "",
        filePath = this.getString(filePath) ?: ""
    )
}

@HiltWorker
class FileUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var fileUploadService: UploadFileService

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            uploadFile(inputData.toFile())
            Result.success()
        } catch (error: Throwable) {

            Result.retry()
        }
    }

    suspend private fun uploadFile(file: SendFile) {
        val service = fileUploadService

        val fileToUpload = File(file.filePath)
        val fileUri = Uri.fromFile(fileToUpload)
        applicationContext.contentResolver.getType(fileUri)?.let {
            val requestFile: RequestBody = RequestBody.create(MediaType.parse(it), fileToUpload)
            val filePart = MultipartBody.Part.createFormData("file", fileToUpload.name, requestFile)
            val call: Call<UploadFileResponse> = service.uploadFile(
                room = file.room,
                userID = App.prefs!!.userID,
                token = App.prefs!!.userToken,
                type = file.type,
                file = filePart
            )
            val response = call.execute()
            if (response.isSuccessful){
                Log.d("FileUploadWorker", "Responce sucess Responce data: ${response.body()}")
            } else {
                Log.d("FileUploadWorker", "Responce Error: ${response.errorBody()} : ${response.code()} Messages ${response.message()}")
            }
        }

    }
}