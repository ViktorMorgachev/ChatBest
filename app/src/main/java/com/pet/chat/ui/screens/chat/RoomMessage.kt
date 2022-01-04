package com.pet.chat.ui.screens.chat

import com.pet.chat.network.data.base.File

sealed class RoomMessage(
    open val isOwn: Boolean,
    open val messageID: Int,
    open val userID: String,
    open val text: String,
    open val date: String?,
    open val file: File?,
) {
    data class SendingMessage(
        override val isOwn: Boolean = false,
        override val messageID: Int,
        override val userID: String,
        override val text: String,
        override val date: String?,
        override val file: File?,
    ) : RoomMessage(isOwn, messageID, userID, text, date, file)

    data class SimpleMessage(
        override val isOwn: Boolean,
        override val messageID: Int,
        override val userID: String,
        override val text: String,
        override val date: String?,
        override val file: File?,
    ) : RoomMessage(isOwn, messageID, userID, text, date, file)
}
