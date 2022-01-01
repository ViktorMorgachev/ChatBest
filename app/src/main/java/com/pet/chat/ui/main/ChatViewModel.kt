package com.pet.chat.ui.main

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pet.chat.App
import com.pet.chat.BuildConfig
import com.pet.chat.R
import com.pet.chat.providers.InternalEvent
import com.pet.chat.providers.InternalEventsProvider
import com.pet.chat.helpers.*
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.Subscriber
import com.pet.chat.network.data.base.File
import com.pet.chat.network.data.base.FilePreview
import com.pet.chat.network.data.base.User
import com.pet.chat.network.data.receive.*
import com.pet.chat.network.data.receive.ChatDelete
import com.pet.chat.network.data.receive.ChatRead
import com.pet.chat.network.workers.*
import com.pet.chat.network.workers.FileUploadConverter.Companion.filePath
import com.pet.chat.network.workers.FileUploadConverter.Companion.fileType
import com.pet.chat.network.workers.FileUploadConverter.Companion.message
import com.pet.chat.network.workers.FileUploadConverter.Companion.roomID
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.providers.UsersProviderImpl
import com.pet.chat.providers.interfaces.UsersProvider
import com.pet.chat.ui.ChatItemInfo
import com.pet.chat.ui.State
import com.pet.chat.ui.RoomMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val eventsProvider: InternalEventsProvider,
    val chatProviderImpl: MultipleChatProviderImpl,
    val usersProvider: UsersProviderImpl
) : ViewModel() {

    lateinit var cameraPermissionContract: ActivityResultLauncher<String>
    val events = MutableStateFlow<EventFromServer>(EventFromServer.NO_INITIALIZED)
    val chats = MutableStateFlow<List<ChatItemInfo>>(listOf())
    var imageUri: Uri? = null

    fun postEventToServer(eventToServer: EventToServer) {
        Log.d("EventToServer", "$eventToServer")
        viewModelScope.launch(Dispatchers.IO) {
            ConnectionManager.postEventToServer(event = eventToServer, error = {
                viewModelScope.launch(Dispatchers.Main) {
                    val resultText =
                        App.instance.applicationContext.getText(R.string.something_went_wrong)
                            .toString() + it
                    Toast.makeText(App.instance.applicationContext, resultText, Toast.LENGTH_LONG)
                        .show()
                }
            })
        }
    }

    init {
        viewModelScope.launch {
            ConnectionManager.subsribe(object : Subscriber {
                override fun post(eventFromServer: EventFromServer) {
                    viewModelScope.launch {
                        events.emit(eventFromServer)
                    }
                }
            })
            val internalEvent = eventsProvider.internalEvents.asStateFlow()
            observeInternalEvent(internalEvent.value)

        }

    }

    private fun observeInternalEvent(internalEvent: InternalEvent?) {
        when (internalEvent) {
            is InternalEvent.OpenFilePreview -> {

            }
            is InternalEvent.LoadingFileError -> {

            }
            is InternalEvent.LoadingFileLoading -> {

            }
        }
    }


    fun updateChat(chatDetails: ChatItemInfo) = viewModelScope.launch(Dispatchers.IO) {
        chatProviderImpl.updateChat(chatDetails)
    }

    fun addTempMessage(messageText: String, file: File, roomID: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val lastMessageID =
                chatProviderImpl.chats.value.firstOrNull { it.roomID == roomID }?.roomMessages?.last()?.messageID
                    ?: 0
            chatProviderImpl.addTempMessage(
                RoomMessage.SendingMessage(
                    isOwn = true,
                    messageID = lastMessageID + 1,
                    userID = App.prefs!!.userID.toString(),
                    text = messageText,
                    date = "current date",
                    file = file
                ), roomID
            )
        }

    // TODO перенести часть логики в другое вью
    fun deleteMessage(message: RoomMessage, roomID: Int) = viewModelScope.launch(Dispatchers.IO) {
        chatProviderImpl.deleteMessageByID(message.messageID, roomID)
    }

    fun deleteMessage(messageDelete: MessageDelete, roomID: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            chatProviderImpl.deleteMessageByID(messageDelete.message.id.toInt(), roomID)
        }

    fun clearChat(chatClear: ChatClear) = viewModelScope.launch(Dispatchers.IO) {
        chatProviderImpl.clearChat(chatClear.room.id.toInt())
    }

    fun updateChat(chatsDetails: List<ChatItemInfo>) = viewModelScope.launch(Dispatchers.IO) {
        chatsDetails.forEach { chatDetail ->
            updateChat(chatDetail)
        }
    }

    fun deleteChat(chatDetails: ChatDelete) = viewModelScope.launch(Dispatchers.IO) {
        chatProviderImpl.deleteChat(chatDetails.room.id.toInt())
    }

    fun fileUploadError(messageID: Int, roomID: Int) = viewModelScope.launch(Dispatchers.IO) {
        chatProviderImpl.fileUploadError(messageID, roomID)
    }


    fun updateChatState(data: ChatRead) = viewModelScope.launch(Dispatchers.IO) {
        chatProviderImpl.updateChatState(data)
    }

    fun updateUserStatus(data: UserOnline) = viewModelScope.launch(Dispatchers.IO) {
        chatProviderImpl.updateChatState(data)
    }

    fun takePicture(context: Context, launchCamera: (Uri) -> Unit) = viewModelScope.launch {
        CoroutineScope(Dispatchers.IO).launch {
            ImageUtils.createImageFile(context)?.also { file ->
                imageUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".fileprovider", file
                )
            }
        }.join()
        imageUri?.let { launchCamera(it) }
    }

    fun startUploadFile(messageText: String, file: File) {
        val workData = workDataOf(
            roomID to file.roomID.toInt(),
            filePath to file.filePath!!,
            fileType to file.type,
            message to messageText
        )
        val workBuilder = OneTimeWorkRequestBuilder<FileUploadWorker>().addTag(fileUploadWorkerTag)
            .setInputData(workData).build()
        val workManager = WorkManager.getInstance(App.instance)
        workManager.enqueue(workBuilder)
        if (!workManager.isWorkScheduled(fileUploadWorkerTag)) {
            workManager.enqueue(workBuilder)
        }
    }

    fun startDownloadPhoto() = viewModelScope.launch(Dispatchers.Default) {

    }

    fun resultAfterCamera(it: Boolean) = viewModelScope.launch(Dispatchers.Default) {

        Log.d(
            "MainActivity",
            "result after camera and last file ${imageUri?.path}"
        )
        if (it) {
            eventsProvider.internalEvents.emit(
                InternalEvent.OpenFilePreview(
                    FilePreview(
                        fileUri = imageUri,
                        filePath = null,
                        openDialog = true
                    )
                )
            )
        }
    }

    fun launchCamera() = viewModelScope.launch(Dispatchers.Main) {
        cameraPermissionContract.launch(Manifest.permission.CAMERA)
    }


}

