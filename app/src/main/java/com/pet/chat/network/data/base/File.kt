package com.pet.chat.network.data.base

import com.pet.chat.App
import com.pet.chat.storage.Prefs
import com.pet.chat.ui.State
import okhttp3.MultipartBody
import retrofit2.http.Part

enum class FileType {
    Photo, File, Video, Voice
}

data class File(
    val roomID: Number,
    val type: String,
    val filePath: String? = null,
    val fileID: Int? = null,
    var state: State,
)


