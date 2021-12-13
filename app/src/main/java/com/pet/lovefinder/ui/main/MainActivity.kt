package com.pet.lovefinder.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.pet.lovefinder.App
import com.pet.lovefinder.network.EventFromServer
import com.pet.lovefinder.network.data.base.ChatDetails
import com.pet.lovefinder.storage.LocalStorage
import com.pet.lovefinder.ui.*
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
    val messages = LocalStorage.messages.collectAsState()

    // TODO рефакторить надо постеменно, убрать это и переписать, плохо
    when (event.value) {
        is EventFromServer.Autorization -> {
            if ((event.value as EventFromServer.Autorization).data.success == true) {
                val data = (event.value as EventFromServer.Autorization).data
                App.prefs?.userID = data.user.id
                LocalStorage.updateChats(data.dialogs.map {
                    ChatDetails(chat = it.chat,
                        roomID = it.room.id.toInt(),
                        users = it.room.users)
                })
                navController.navigate("Chats")
            }
        }
        is EventFromServer.MessageNewEvent -> {
            val data = (event.value as EventFromServer.MessageNewEvent).data
            LocalStorage.updateChats(ChatDetails(chat = data.chat,
                roomID = data.room.id.toInt(),
                users = data.room.users))
            LocalStorage.updateMessages(listOf(data.message.toRoomMessage()))
        }
        is EventFromServer.ChatHistoryEvent -> {
            val data = (event.value as EventFromServer.ChatHistoryEvent).data
            LocalStorage.updateMessages(data.messages.map { it.toRoomMessage() })
            // TODO Тут же передавать айди комнаты
            navController.navigate("Room")
        }
        is EventFromServer.ChatDelete -> {
            val data = (event.value as EventFromServer.ChatDelete).data
            LocalStorage.deleteChat(ChatDetails(chat = data.chat,
                roomID = data.room.id.toInt(),
                users = data.room.users))
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
                deleteChat = { viewModel.deleteChat(it) },
                openChat = { viewModel.getChatHistory(it) })
        }
        composable(Screen.CreateChat.route) {
            CreateChatScreen(value = event.value,
                createChat = { viewModel.createChat(it) },
                navController = navController)
        }
        // TODO думаю хорошая идея будет пробросить именно event в composables и в них уже слушать события если что
        composable(Screen.Room.route) {
            ChatList(sendMessage = { viewModel.sendMesage(it) }, messages = messages.value)
        }
    }
}




