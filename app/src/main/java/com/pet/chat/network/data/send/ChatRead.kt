package com.pet.chat.network.data.send

import com.google.gson.annotations.SerializedName

data class ChatRead(
    @SerializedName("room_id")
    var roomId: Number,
)