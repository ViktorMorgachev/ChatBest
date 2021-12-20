package com.pet.chat.network.data.send

import com.pet.chat.App
import com.pet.chat.storage.Prefs
import okhttp3.MultipartBody
import retrofit2.http.Part

enum class FileType {
    Photo, File, Video, Voice
}

data class File(
    val room: Number,
    val type: String,
    val filePath: String,
)
