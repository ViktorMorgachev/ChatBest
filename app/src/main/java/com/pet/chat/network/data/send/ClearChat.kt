package com.pet.chat.network.data.send

import com.google.gson.annotations.SerializedName

data class ClearChat(
    @SerializedName("room_id")
    val roomID: Number,
)