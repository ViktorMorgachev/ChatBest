package com.pet.chat.network.data.base

import com.pet.chat.ui.screens.chat.State

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


