package com.pet.chat.network.data.send

data class ChatNew(
    var attachmentId: String?,
    var text: String,
    var userId: String
)