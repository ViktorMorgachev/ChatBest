package com.pet.chat.network.data.responce

import com.google.gson.annotations.SerializedName
import com.pet.chat.network.data.Attachment

data class UploadFileResponse(
    val success: Boolean,
    val attachment: Attachment,
)