package com.pet.chat.events

import android.net.Uri
import com.pet.chat.network.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

sealed class InternalEvent() {
    data class OpenFilePreview(
        val fileUri: Uri?,
        val filePath: String?,
        val openDialog: Boolean = true,
    ) : InternalEvent()

    object None: InternalEvent()
}
