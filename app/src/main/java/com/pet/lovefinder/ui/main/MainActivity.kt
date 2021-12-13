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
import com.pet.lovefinder.network.data.receive.ChatDelete
import com.pet.lovefinder.network.data.send.UserAuth
import com.pet.lovefinder.network.data.toChatItemInfo
import com.pet.lovefinder.ui.ViewDataStorage
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
                    messages = messages,
                    roomID = roomID.toInt(),
                    navController = navController)
            }

        }
    }

    /* if (App.prefs!!.userID > 1) {
         val authData = UserAuth(id = App.prefs!!.userID, token = App.prefs!!.userToken!!)
         viewModel.login(authData)
         navController.navigate("Chats")
     }*/

}

fun observe(event: State<EventFromServer>) {
    when (event.value) {
        is EventFromServer.Autorization -> {
            if ((event.value as EventFromServer.Autorization).data.success == true) {
                val data = (event.value as EventFromServer.Autorization).data
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
        is EventFromServer.ChatDelete -> {
            val data = (event.value as EventFromServer.ChatDelete).data
            ViewDataStorage.deleteChat(ChatDelete(chat = data.chat, room = data.room))
        }
    }
}





