package com.pet.lovefinder.network.data.send

import com.google.gson.annotations.SerializedName

data class ChatDelete(
    @SerializedName("room_id")
    var roomId: Number,
)