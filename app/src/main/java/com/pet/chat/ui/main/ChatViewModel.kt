package com.pet.chat.ui.main

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pet.chat.App
import com.pet.chat.BuildConfig
import com.pet.chat.R
import com.pet.chat.events.InternalEvent
import com.pet.chat.events.InternalEventsProvider
import com.pet.chat.helpers.*
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.Subscriber
import com.pet.chat.network.data.base.File
import com.pet.chat.network.data.base.User
import com.pet.chat.network.data.receive.*
import com.pet.chat.network.data.receive.ChatDelete
import com.pet.chat.network.data.receive.ChatRead
import com.pet.chat.network.workers.*
import com.pet.chat.network.workers.FileUploadConverter.Companion.filePath
import com.pet.chat.network.workers.FileUploadConverter.Companion.fileType
import com.pet.chat.network.workers.FileUploadConverter.Companion.message
import com.pet.chat.network.workers.FileUploadConverter.Companion.roomID
import com.pet.chat.ui.ChatItemInfo
import com.pet.chat.ui.State
import com.pet.chat.ui.RoomMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    val events = MutableStateFlow<EventFromServer>(EventFromServer.NO_INITIALIZED)
    val chats = MutableStateFlow<List<ChatItemInfo>>(listOf())
    val messages = MutableStateFlow<List<RoomMessage>>(listOf())
    val users = MutableStateFlow<List<User>>(mutableListOf())
    var imageUri: Uri? = null

    // Потом нужног будет это вырезать
    // Проблема при возвращении назад с камеры, нужно будет поправить по хорошему
    var currentRoom = -1

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
        }

    }

    fun postInternalAction(internalEvent: InternalEvent) = viewModelScope.launch(Dispatchers.IO) {
        InternalEventsProvider.internalEvents.emit(internalEvent)
    }

    fun addMessage(roomMessage: RoomMessage) = viewModelScope.launch(Dispatchers.IO) {
        messages.value = messages.value.addLast(roomMessage)
        updateChat()
    }

    fun addTempMessage(messageText: String, file: File) = viewModelScope.launch(Dispatchers.IO) {
        val lastMessageID = messages.value.last().messageID + 1;
        messages.value = messages.value.addLast(RoomMessage.SendingMessage(isOwn = true,
            messageID = lastMessageID,
            userID = App.prefs!!.userID.toString(),
            text = messageText,
            date = "current date",
            file = file))
        updateChat()
    }

    fun fileUploadError(messageID: Int) = viewModelScope.launch(Dispatchers.IO) {
        val message =
            (messages.value.find { it is RoomMessage.SendingMessage && it.messageID == messageID } as RoomMessage.SendingMessage)
        message.file?.state = State.Error
        messages.value = messages.value.removeWithInstance(message)
        messages.value = messages.value.addLast(message)
        updateChat()
    }

    fun updateChat(chatDetails: ChatItemInfo) = viewModelScope.launch(Dispatchers.IO) {
        val actualChat = chats.value.find { it.roomID == chatDetails.roomID }
        if (actualChat != null) {
            val newList = actualChat.roomMessages.plus(chatDetails.roomMessages)
            actualChat.roomMessages = newList
            messages.value = newList
        } else {
            messages.value = chatDetails.roomMessages
            chats.value = chats.value.addLast(chatDetails)
        }
        updateChat()
    }

    fun deleteMessage(roomMessage: RoomMessage) = viewModelScope.launch(Dispatchers.IO) {
        messages.value = messages.value.removeWithInstance(roomMessage)
        updateChat()
    }

    fun deleteMessage(messageDelete: MessageDelete) = viewModelScope.launch(Dispatchers.IO) {
        val currentChat = chats.value.first { messageDelete.room.id.toInt() == it.roomID }
        var roomMessages = currentChat.roomMessages
        roomMessages.firstOrNull { it.messageID == messageDelete.message.id.toInt() }?.let {
            roomMessages = roomMessages.removeWithInstance(it)
        }
        messages.value = roomMessages
        currentChat.roomMessages = roomMessages
        updateChat()
    }

    fun clearChat(chatClear: ChatClear) = viewModelScope.launch(Dispatchers.IO) {
        chats.value.find { it.roomID == chatClear.room.id.toInt() }?.let {
            chats.value = chats.value.removeWithInstance(it)
            updateChat()
        }
    }

    fun updateChat(chatsDetails: List<ChatItemInfo>) = viewModelScope.launch(Dispatchers.IO) {
        chatsDetails.forEach { chatDetail ->
            val lastListMessagesInfo = chats.value.find { it.roomID == chatDetail.roomID }
            if (lastListMessagesInfo != null) {
                lastListMessagesInfo.roomMessages =
                    lastListMessagesInfo.roomMessages.addAll(chatDetail.roomMessages)
            } else {
                chats.value = chats.value.addLast(chatDetail)
                messages.value = chats.value.get(0).roomMessages
            }
        }
        updateChat()
    }

    suspend fun updateChat() {

        val newChats = mutableListOf<ChatItemInfo>()
        chats.value.forEach {
            newChats.add(it.copy())
        }
        chats.value = newChats.toList()
        val emitResult = chats.tryEmit(chats.value)
        messages.emit(messages.value)
        Log.d("UpdateChat", "Chat: ${chats.value} EmitResult $emitResult")
    }

    fun deleteChat(chatDetails: ChatDelete) = viewModelScope.launch(Dispatchers.IO) {
        chats.value.firstOrNull { it.roomID == chatDetails.room.id.toInt() }?.let {
            chats.value = chats.value.removeWithInstance(it)
        }
        updateChat()
    }

    fun updateChatState(data: ChatRead) = viewModelScope.launch(Dispatchers.IO) {
        chats.value.find { it.roomID == data.room.id.toInt() }?.let {
            it.unreadCount = 0
        }
        updateChat()
    }

    fun updateUserStatus(data: UserOnline) = viewModelScope.launch(Dispatchers.IO) {}

    fun takePicture(context: Context, launchCamera: (Uri) -> Unit) = viewModelScope.launch {
        CoroutineScope(Dispatchers.IO).launch {
            ImageUtils.createImageFile(context)?.also { file ->
                imageUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".fileprovider", file)
            }
        }.join()
        imageUri?.let { launchCamera(it) }
    }

    fun startUploadFile(messageText: String, file: File) {
        val workData = workDataOf(roomID to file.roomID.toInt(),
            filePath to  file.filePath!!,
            fileType to file.type,
            message to messageText
        )
        val workBuilder = OneTimeWorkRequestBuilder<FileUploadWorker>().addTag(fileUploadWorkerTag).setInputData(workData).build()
        val workManager = WorkManager.getInstance(App.instance)
        workManager.enqueue(workBuilder)
        if (!workManager.isWorkScheduled(fileUploadWorkerTag)) {
            workManager.enqueue(workBuilder)
        }
    }

    fun startDownloadPhoto()= viewModelScope.launch(Dispatchers.IO){

    }
}

