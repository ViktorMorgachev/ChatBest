package com.pet.chat.events

import android.net.Uri

sealed class InternalEvent() {
    data class OpenFilePreview(
        val fileUri: Uri?,
        val filePath: String?,
        val openDialog: Boolean = true,
    ) : InternalEvent()

    object None: InternalEvent()
}
