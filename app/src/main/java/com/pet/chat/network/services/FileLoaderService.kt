package com.pet.chat.network.services

import com.pet.chat.network.data.responce.UploadFileResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadFileService {
    @Multipart
    @POST("upload")
    suspend fun uploadFile(
        @Part("room") room: Number,
        @Part("user_id") userID: Number,
        @Part("token") token: String,
        @Part("type") type: String,
        @Part("file") file: MultipartBody.Part?,
    ): UploadFileResponse
}