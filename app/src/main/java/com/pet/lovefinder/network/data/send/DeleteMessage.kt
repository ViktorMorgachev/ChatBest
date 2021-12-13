package com.pet.lovefinder.network.data.send

import com.google.gson.annotations.SerializedName

data class DeleteMessage(
    @SerializedName("message_id")
    var messageId: Number,
)