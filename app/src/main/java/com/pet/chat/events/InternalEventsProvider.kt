package com.pet.chat.events

import android.net.Uri
import com.pet.chat.network.data.base.Attachment
import com.pet.chat.network.data.base.FilePreview
import com.pet.chat.network.data.responce.LoadFileResponse
import com.pet.chat.network.workers.SendingFile
import com.pet.chat.ui.State
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class InternalEvent() {
    data class OpenFilePreview(val filePreview: FilePreview, ) : InternalEvent()
    data class LoadingFileError(val sendingFile: SendingFile) : InternalEvent()
    data class LoadingFileLoading(val sendingFile: SendingFile) : InternalEvent()
    data class LoadingFileSuccess(val roomID: Int, val attachment: Attachment) : InternalEvent()

}

@Singleton
class InternalEventsProvider @Inject constructor() {
    val internalEvents = MutableStateFlow<InternalEvent?>(null)
}
