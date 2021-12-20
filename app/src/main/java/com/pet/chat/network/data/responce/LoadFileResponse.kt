package com.pet.chat.network.data.responce

import com.pet.chat.network.data.base.Attachment

data class LoadFileResponse(
    val success: Boolean,
    val attachment: Attachment,
)