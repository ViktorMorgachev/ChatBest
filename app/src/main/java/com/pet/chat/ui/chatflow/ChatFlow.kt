package com.pet.chat.ui.chatflow

import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pet.chat.App
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.send.ChatDelete
import com.pet.chat.ui.*
import com.pet.chat.ui.main.ChatViewModel
import com.pet.chat.ui.screens.autorization.AutorizationScreen
import com.pet.chat.ui.screens.autorization.AutorizationViewModel
import com.pet.chat.ui.screens.chat.Room
import com.pet.chat.ui.screens.chat.MessagesViewModel
import com.pet.chat.ui.screens.chats.ChatsScreen
import com.pet.chat.ui.screens.chats.ChatsViewModel

fun NavGraphBuilder.chatFlow(
    navController: NavController,
    chatViewModel: ChatViewModel
) {
    composable(Screen.Autorization.route) {
        val viewModel = hiltViewModel<AutorizationViewModel>()
        AutorizationScreen(
            onAuthEvent = {
                viewModel.authorize(EventToServer.AuthEvent(it))
            },
            viewModel = viewModel,
            navController = navController
        ).also {
            App.states?.lastRooom = -1
        }
    }
    composable(Screen.Chats.route) {
        val chatsViewModel = hiltViewModel<ChatsViewModel>()
        ChatsScreen(
            navController = navController,
            deleteChat = { chatsViewModel.deleteChat(ChatDelete(it.roomId.toInt())) },
            viewModel = chatsViewModel
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
        Room(
            roomID = roomID.toInt(),
            navController = navController,
            scope = rememberCoroutineScope(),
            cameraLauncher = { chatViewModel.launchCamera() },
            viewModel = messagesViewModel,
            actionProvider = messagesViewModel.actionProvider
        ).also {
            App.states?.lastRooom = roomID.toInt()
        }
    }
}