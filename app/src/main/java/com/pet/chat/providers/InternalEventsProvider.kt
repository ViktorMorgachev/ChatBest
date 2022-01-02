package com.pet.chat.providers

import com.pet.chat.network.data.base.Attachment
import com.pet.chat.network.data.base.FilePreview
import com.pet.chat.network.workers.SendingFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class InternalEvent() {
    data class OpenFilePreview(val filePreview: FilePreview) : InternalEvent()
    data class LoadingFileError(val sendingFile: SendingFile) : InternalEvent()
    data class LoadingFileLoading(val sendingFile: SendingFile) : InternalEvent()
    data class LoadingFileSuccess(val roomID: Int, val attachment: Attachment) : InternalEvent()
}

@Singleton
class InternalEventsProvider @Inject constructor() {
    val internalEvents = MutableStateFlow<InternalEvent?>(null)
    fun postInternalEvent(event: InternalEvent) = runBlocking(Dispatchers.Default) {
        internalEvents.emit(event)
    }
}
