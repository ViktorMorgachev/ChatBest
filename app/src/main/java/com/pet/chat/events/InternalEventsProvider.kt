package com.pet.chat.events

import android.net.Uri
import com.pet.chat.network.data.responce.LoadFileResponse
import kotlinx.coroutines.flow.MutableStateFlow

sealed class InternalEvent() {
    data class OpenFilePreview(
        val fileUri: Uri?,
        val filePath: String?,
        val openDialog: Boolean = true,
    ) : InternalEvent()

    object None : InternalEvent()
    data class FileErrorUpload(val messageID: Int) : InternalEvent()
    data class FileSuccessUpload(val messageID: Int, val fileLoadResponse: LoadFileResponse?) : InternalEvent()
}

class InternalEventsProvider{
    val internalEvents = MutableStateFlow<InternalEvent>(InternalEvent.None)
}
