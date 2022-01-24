package com.pet.chat.ui.chatflow

import android.util.Log
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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
        val roomID = backStackEntry.arguments?.getString("roomID")?.toInt()
        requireNotNull(roomID) { "roomID parameter wasn't found. Please make sure it's set!" }
        messagesViewModel.curentRoomID = roomID.toInt()
        val roommateID = messagesViewModel.chatProviderImpl.chats.value.firstOrNull { it.roomID == roomID.toInt() }?.usersIDs?.firstOrNull { App.prefs?.userID != it }
        requireNotNull(roommateID) { "roommateID parameter wasn't found. Please make sure it's set!" }
        Room(
            roomID = roomID,
            scope = rememberCoroutineScope(),
            cameraLauncher = { chatViewModel.launchCamera() },
            viewModel = messagesViewModel,
            actionProvider = messagesViewModel.actionProvider,
            toolbar = defaultMockToolbar.copy(onBackPressed = {navController.navigateUp()},
                actions = listOf(){
                IconButton(onClick = { messagesViewModel.actionProvider.clearChatAction() }) {
                    Icon(Icons.Filled.ClearAll, contentDescription = "Clear")
                }
            }, text = "Пользователь $roommateID")
        ).also {
            App.states?.lastRooom = roomID.toInt()
        }
    }
}