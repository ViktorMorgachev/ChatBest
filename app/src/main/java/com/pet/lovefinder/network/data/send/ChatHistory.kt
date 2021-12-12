package com.pet.lovefinder.network.data.send

import com.google.gson.annotations.SerializedName

data class ChatHistory(
    var lastId: Number?,
    var limit: Number?,
    var roomId: Number,
)