package com.pet.chat.network.data.receive


import com.google.gson.annotations.SerializedName

data class UserOnline(
    var id: Number,
    var online: Boolean
)