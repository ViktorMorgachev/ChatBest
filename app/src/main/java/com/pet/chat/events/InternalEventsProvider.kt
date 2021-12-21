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
}

object InternalEventsProvider{
    val internalEvents = MutableStateFlow<InternalEvent>(InternalEvent.None)
}
