package com.pet.chat.providers.interfaces

interface MultipleMessagesProvider<T> {
    fun addMessage(message: T, roomID: Int)
    fun addTempMessage(data: T, roomID: Int)
    fun deleteMessageByID(messageID: Int, roomID: Int)
    fun fileUploadError(messageID: Int, roomID: Int)
}