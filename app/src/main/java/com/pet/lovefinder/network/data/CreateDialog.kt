package com.pet.lovefinder.network.data

data class CreateDialog(
    val user_id: Number,
    val text: String,
    val attachment_id: Number? = null,
)
