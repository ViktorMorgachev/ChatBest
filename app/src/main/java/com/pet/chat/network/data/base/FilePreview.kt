package com.pet.chat.network.data.base

import android.net.Uri

data class FilePreview(val fileUri: Uri?,
    val filePath: String?,
    val openDialog: Boolean = true)
