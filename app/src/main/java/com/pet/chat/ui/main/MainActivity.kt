package com.pet.chat.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import com.pet.chat.R
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.data.receive.ChatDelete
import com.pet.chat.network.data.toChatItemInfo
import com.pet.chat.ui.ViewDataStorage
import com.pet.chat.ui.*
import com.pet.chat.ui.theme.LoveFinderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    @Composable
    fun MyApp(viewModel: ChatViewModel) {
        val navController = rememberNavController()
        val event = viewModel.events.collectAsState()
        val chats = ViewDataStorage.chats.collectAsState()

        observe(event)

        NavHost(navController = navController, startDestination = Screen.Autorization.route) {
            composable(Screen.Autorization.route) {
                AutorizationScreen(onAuthEvent = { viewModel.login(it) },
                    navController = navController)
            }
            composable(Screen.Chats.route) {
                ChatsScreen(
                    navController = navController,
                    deleteChat = { viewModel.deleteChat(it) },
                    openChat = {
                        viewModel.getChatHistory(it)
                        navController.navigate(Screen.Room.createRoute(it.roomId.toString()))
                    }, chats = chats.value)
            }
            composable(Screen.CreateChat.route) {
                CreateChatScreen(value = event.value,
                    createChat = { viewModel.createChat(it) },
                    navController = navController)
            }
            composable(Screen.Room.route) { backStackEntry ->
                val roomID = backStackEntry.arguments?.getString("roomID")
                requireNotNull(roomID) { "roomID parameter wasn't found. Please make sure it's set!" }
                chats.value.firstOrNull { it.roomID == roomID.toInt() }?.let {
                    val messages = it.roomMessages
                    Chat(sendMessage = { viewModel.sendMesage(it) },
                        messages = messages.toList(),
                        roomID = roomID.toInt(),
                        navController = navController,
                        clearChat = { viewModel.clearChat(roomID.toInt()) },
                        event = event.value,
                        deleteMessage = { viewModel.deleteMessage(it) })
                }

            }
        }

    }

    @Composable
    fun observe(event: State<EventFromServer>) {
        try {
            print("EventFromServer: ${event.value}")
            when (event.value) {
                is EventFromServer.AutorizationEvent -> {
                    if ((event.value as EventFromServer.AutorizationEvent).data.success == true) {
                        val data = (event.value as EventFromServer.AutorizationEvent).data
                        ViewDataStorage.updateChat(data.dialogs.map { it.toChatItemInfo() })
                    }
                }
                is EventFromServer.MessageNewEvent -> {
                    val data = (event.value as EventFromServer.MessageNewEvent).data
                    ViewDataStorage.updateChat(data.toChatItemInfo())
                }
                is EventFromServer.ChatHistoryEvent -> {
                    val data = (event.value as EventFromServer.ChatHistoryEvent).data
                    data.toChatItemInfo()?.let { chatItemInfo ->
                        ViewDataStorage.updateChat(chatItemInfo)
                    }

                }
                is EventFromServer.ChatDeleteEvent -> {
                    val data = (event.value as EventFromServer.ChatDeleteEvent).data
                    ViewDataStorage.deleteChat(ChatDelete(chat = data.chat, room = data.room))
                }
                is EventFromServer.ChatClearEvent -> {
                    val data = (event.value as EventFromServer.ChatClearEvent).data
                    ViewDataStorage.clearChat(data)
                }
                is EventFromServer.MessageDeleteEvent -> {
                    val data = (event.value as EventFromServer.MessageDeleteEvent).data
                    ViewDataStorage.deleteMessage(data)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Toast.makeText(this,
                stringResource(id = R.string.something_went_wrong),
                Toast.LENGTH_LONG).show()
        }
    }
}







