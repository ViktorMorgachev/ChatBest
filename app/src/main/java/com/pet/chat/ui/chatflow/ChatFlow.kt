package com.pet.chat.ui.chatflow

import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pet.chat.App
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.base.File
import com.pet.chat.network.data.send.ClearChat
import com.pet.chat.network.data.send.DeleteMessage
import com.pet.chat.ui.*
import com.pet.chat.ui.main.ChatViewModel
import com.pet.chat.ui.main.MessagesViewModel

fun NavGraphBuilder.chatFlow(
    navController: NavController,
    chatViewModel: ChatViewModel
) {
    composable(Screen.Autorization.route) {
        AutorizationScreen(
            onAuthEvent = {
                chatViewModel.postEventToServer(EventToServer.AuthEvent(it))
            },
            viewModel = chatViewModel
        ).also {
            App.states?.lastRooom = -1
        }
    }
    composable(Screen.Chats.route) {
        ChatsScreen(
            navController = navController,
            deleteChat = { chatViewModel.postEventToServer(EventToServer.DeleteChat(it)) },
            openChat = {
                chatViewModel.postEventToServer(EventToServer.GetChatHistory(it))
            }, viewModel = chatViewModel
        ).also {
            App.states?.lastRooom = -1
        }
    }
    composable(Screen.CreateChat.route) {
        CreateChatScreen(
            createChat = { chatViewModel.postEventToServer(EventToServer.CreateChatEvent(it)) },
            navController = navController
        ).also {
            App.states?.lastRooom = -1
        }
    }
    composable(Screen.Room.route) { backStackEntry ->
        val messagesViewModel = hiltViewModel<MessagesViewModel>()
        val roomID = backStackEntry.arguments?.getString("roomID")
        requireNotNull(roomID) { "roomID parameter wasn't found. Please make sure it's set!" }
        messagesViewModel.curentRoomID = roomID.toInt()
        Chat(
            sendMessage = { chatViewModel.postEventToServer(EventToServer.SendMessageEvent(it)) },
            roomID = roomID.toInt(),
            navController = navController,
            clearChat = {
                chatViewModel.postEventToServer(EventToServer.ClearChatEvent(ClearChat(roomID.toInt())))
            },
            deleteMessageAction = {
                if (it is RoomMessage.SendingMessage) {
                    chatViewModel.deleteMessage(it, roomID.toInt())
                } else {
                    chatViewModel.postEventToServer(
                        EventToServer.DeleteMessageEvent(
                            DeleteMessage(it.messageID)
                        )
                    )
                }
            },
            eventChatRead = { chatViewModel.postEventToServer(EventToServer.ChatReadEvent(it)) },
            scope = rememberCoroutineScope(),
            cameraLauncher = { chatViewModel.launchCamera() },
            viewModel = messagesViewModel,
            tryLoadFileAction = {
                val file = File(
                    roomID = it.file!!.roomID,
                    type = it.file.type,
                    filePath = it.file.filePath,
                    fileID = it.file.fileID,
                    state = it.file.state
                )
                chatViewModel.startUploadFile(it.text, file)
            },
            tryToDownLoadAction = {
                chatViewModel.startDownloadPhoto()
            },
            applyMessageAction = { text, file ->
                chatViewModel.addTempMessage(text, file, roomID.toInt())
            },
            chatViewModel = chatViewModel
        ).also {
            App.states?.lastRooom = roomID.toInt()
        }
    }
}