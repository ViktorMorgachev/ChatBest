package com.pet.lovefinder.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.pet.lovefinder.network.EventFromServer
import com.pet.lovefinder.network.data.base.ChatDetails
import com.pet.lovefinder.network.data.base.Messages
import com.pet.lovefinder.storage.LocalStorage
import com.pet.lovefinder.ui.AutorizationScreen
import com.pet.lovefinder.ui.ChatsScreen
import com.pet.lovefinder.ui.CreateChatScreen
import com.pet.lovefinder.ui.Screen
import com.pet.lovefinder.ui.theme.LoveFinderTheme

class MainActivity : ComponentActivity() {

    val eventViewModel by viewModels<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoveFinderTheme {
                MyApp(eventViewModel)
            }
        }

    }
}

@Composable
fun MyApp(viewModel: ChatViewModel = ChatViewModel()) {
    val navController = rememberNavController()
    val event = viewModel.events.collectAsState()

    when (event.value) {
        is EventFromServer.Autorization -> {
            if ((event.value as EventFromServer.Autorization).data.success == true) {
                val data = (event.value as EventFromServer.Autorization).data.dialogs
                data.forEach { dialog ->
                    LocalStorage.updateChats(ChatDetails(chat = dialog.chat,
                        roomID = dialog.room.id.toInt(),
                        users = dialog.room.users))
                }

                navController.navigate("Chats")
            }
        }
        is EventFromServer.MessageNewEvent -> {
            val data = (event.value as EventFromServer.MessageNewEvent).data
            LocalStorage.updateChats(ChatDetails(chat = data.chat,
                roomID = data.room.id.toInt(),
                users = data.room.users))
            LocalStorage.updateMessages(listOf(Messages(messages = listOf(data.message),
                roomID = data.room.id.toInt())))
        }
        is EventFromServer.ChatHistoryEvent -> {
            // TODO нужно в будущем конвертацию вынести, пока пойдёт
            val data = (event.value as EventFromServer.ChatHistoryEvent).data
            LocalStorage.updateMessages(listOf(Messages(messages = data.messages,
                roomID = data.room.id.toInt())))
        }
    }
    NavHost(navController = navController, startDestination = Screen.Autorization.route) {
        composable(Screen.Autorization.route) {
            AutorizationScreen(value = event.value,
                onAuthEvent = { viewModel.login(it) },
                navController = navController)
        }
        composable(Screen.Chats.route) {
            ChatsScreen(value = event.value,
                navController = navController,
                deleteChat = {},
                openChat = { viewModel.getChatHistory(it) })
        }
        composable(Screen.CreateChat.route) {
            CreateChatScreen(value = event.value,
                createChat = { viewModel.createChat(it) },
                navController = navController)
        }
    }
}




