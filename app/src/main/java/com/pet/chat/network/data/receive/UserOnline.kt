package com.pet.chat.network.data.receive


import com.google.gson.annotations.SerializedName

data class UserOnline(
    @SerializedName("id")
    var userID: Number,
    var online: Boolean
)