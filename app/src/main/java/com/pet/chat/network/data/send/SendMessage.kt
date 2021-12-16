package com.pet.chat.network.data.send

import com.google.gson.annotations.SerializedName

data class SendMessage(
    @SerializedName("attachment_id")
    var attachmentId: Number?,
    @SerializedName("room_id")
    var roomId: Number,
    var text: String,
)