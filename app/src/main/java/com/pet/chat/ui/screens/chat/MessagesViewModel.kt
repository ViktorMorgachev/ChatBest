package com.pet.chat.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pet.chat.App
import com.pet.chat.helpers.fileUploadWorkerTag
import com.pet.chat.helpers.isWorkScheduled
import com.pet.chat.helpers.workDataOf
import com.pet.chat.network.ConnectionManager
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.ViewState
import com.pet.chat.network.data.base.File
import com.pet.chat.network.data.send.ChatRead
import com.pet.chat.network.data.send.ClearChat
import com.pet.chat.network.data.send.DeleteMessage
import com.pet.chat.network.data.send.SendMessage
import com.pet.chat.network.workers.FileUploadConverter
import com.pet.chat.network.workers.FileUploadWorker
import com.pet.chat.providers.InternalEventsProvider
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.providers.ViewStateProviderImpl
import com.pet.chat.providers.interfaces.EventFromServerProvider
import com.pet.chat.providers.interfaces.EventFromServerProviderImpl
import com.pet.chat.providers.interfaces.ViewStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    val chatProviderImpl: MultipleChatProviderImpl,
    val viewStateProvider: ViewStateProvider,
    val eventFromServerProvider: EventFromServerProvider,
    val connectionManager: ConnectionManager,
    val internalEventsProvider: InternalEventsProvider
) : ViewModel() {

    var curentRoomID = -1
    val messages = MutableStateFlow<List<RoomMessage>>(listOf())
    var actionProvider: ActionProvider
    init {
        actionProvider = ActionProvider()
        viewModelScope.launch(Dispatchers.IO) {

            eventFromServerProvider.events.collect {
                viewStateProvider.postViewState(reduce(it))
            }

            val data =
                chatProviderImpl.chats.asStateFlow().value.firstOrNull { it.roomID == curentRoomID }?.roomMessages
                    ?: listOf()
            messages.emit(data)
        }
    }

    private fun reduce(eventFromServer: EventFromServer): ViewState {
        return when (eventFromServer) {
            is EventFromServer.ChatHistoryEvent -> {
                val data = eventFromServer.data
                if (data.messages.isEmpty()) {
                    ViewState.StateNoItems
                } else ViewState.Display(data)
            }
            is EventFromServer.ChatClearEvent -> {
                val data = eventFromServer.data
                if (data.room.id.toInt() == curentRoomID) {
                    return ViewState.StateNoItems
                } else return ViewState.Display(data = chatProviderImpl.chats)
            }
            else -> {
                return ViewState.StateLoading
            }
        }
    }

    fun deleteSimpleMessage(message: RoomMessage) = viewModelScope.launch(Dispatchers.IO) {
        connectionManager.postEventToServer(EventToServer.DeleteMessageEvent(DeleteMessage(message.messageID)), error = {
            viewStateProvider.postViewState(ViewState.Error(it))
        })
    }


    fun deleteSendingMessage(message: RoomMessage) = viewModelScope.launch(Dispatchers.IO) {
        chatProviderImpl.deleteMessageByID(message.messageID, roomID = curentRoomID)
    }

    fun clearChat(roomID: Int = curentRoomID) = viewModelScope.launch(Dispatchers.IO) {
        connectionManager.postEventToServer(EventToServer.ClearChatEvent(ClearChat(roomID = curentRoomID)), error = {
            viewStateProvider.postViewState(ViewState.Error(it))
        })
    }

    fun charReadEvent(chatRead: ChatRead) = viewModelScope.launch(Dispatchers.IO){
        connectionManager.postEventToServer(EventToServer.ChatReadEvent(chatRead)){
            viewStateProvider.postViewState(ViewState.Error(it))
        }
    }

    fun sendMessage(sendMessage: SendMessage) = viewModelScope.launch(Dispatchers.IO) {
        connectionManager.postEventToServer(EventToServer.SendMessageEvent(sendMessage), error = {
            viewStateProvider.postViewState(ViewState.Error(it))
        })
    }

    fun addTempMessage(messageText: String, file: File, roomID: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val lastMessageID = chatProviderImpl.chats.value.firstOrNull { it.roomID == roomID }?.roomMessages?.last()?.messageID ?: 0
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

    fun getChatHistory(eventToServer: EventToServer.GetChatHistory) =
        viewModelScope.launch(Dispatchers.IO) {
            connectionManager.postEventToServer(event = eventToServer, error = {
                viewModelScope.launch(Dispatchers.Main) {
                    viewStateProvider.viewState.emit(ViewState.Error(it))
                }
            })
        }

    fun startUploadFile(messageText: String, file: File) {
        val workData = workDataOf(
            FileUploadConverter.roomID to file.roomID.toInt(),
            FileUploadConverter.filePath to file.filePath!!,
            FileUploadConverter.fileType to file.type,
            FileUploadConverter.message to messageText
        )
        val workBuilder = OneTimeWorkRequestBuilder<FileUploadWorker>().addTag(fileUploadWorkerTag)
            .setInputData(workData).build()
        val workManager = WorkManager.getInstance(App.instance)
        workManager.enqueue(workBuilder)
        if (!workManager.isWorkScheduled(fileUploadWorkerTag)) {
            workManager.enqueue(workBuilder)
        }
    }

    fun startDownloadFile() = viewModelScope.launch(Dispatchers.Default) {

    }

    fun resultAfterCamera(){

    }

    // Уменьшаем количество action которые можно отправить в composable
    inner class ActionProvider {
        fun sendMessageAction(message: SendMessage) {
            sendMessage(sendMessage = message)
        }

        fun applyMessageAction(text: String, file: File) {
            addTempMessage(text, file, curentRoomID)
            startUploadFile(messageText = text, file = file)
        }


        fun tryUploadFileAction(data: RoomMessage.SendingMessage) {
            val file = File(
                roomID = data.file!!.roomID,
                type = data.file.type,
                filePath = data.file.filePath,
                fileID = data.file.fileID,
                state = data.file.state
            )
            startUploadFile(data.text, file)
        }

        fun clearChatAction() = clearChat(roomID = curentRoomID)

        fun deleteMessageAction(data: RoomMessage.SimpleMessage)= deleteSimpleMessage(message =data)

        fun deleteMessageAction(data: RoomMessage.SendingMessage)= deleteSendingMessage(message = data)

        fun tryToDownLoadAction(data: RoomMessage.SimpleMessage) = startDownloadFile()

        fun chatReadEvent(data: ChatRead) = charReadEvent(data)

        fun resultAfterCamera(resultAction: Boolean = true){

        }
    }
}