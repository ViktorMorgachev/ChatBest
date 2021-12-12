package com.pet.lovefinder.network.data.send


import com.google.gson.annotations.SerializedName

data class ChatNew(
    var attachmentId: String?,
    var text: String,
    var userId: String
)