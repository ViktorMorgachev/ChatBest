package com.pet.chat.network.data.send

import com.google.gson.annotations.SerializedName

data class ChatHistory(
    @SerializedName("last_id")
    var lastId: Number?,
    var limit: Number?,
    @SerializedName("room_id")
    var roomId: Number,
)