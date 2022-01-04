package com.pet.chat.ui.screens.chats

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pet.chat.R
import com.pet.chat.helpers.retryAction
import com.pet.chat.network.data.ViewState
import com.pet.chat.network.data.base.Dialog
import com.pet.chat.network.data.receive.MessageNew
import com.pet.chat.network.data.send.ChatDelete
import com.pet.chat.network.data.send.ChatHistory
import com.pet.chat.ui.ErrorScreen
import com.pet.chat.ui.LoadingScreen
import com.pet.chat.ui.NoItemsView
import com.pet.chat.ui.Screen
import com.pet.chat.ui.screens.chat.toSimpleMessage
import com.pet.chat.ui.theme.ChatTheme
import com.pet.chat.ui.theme.Shapes

fun Dialog.toChatItemInfo(): ChatItemInfo {
    val usersIDs = mutableListOf<Int>()
    this.room.users.forEach {
        usersIDs.add(it.id.toInt())
    }
    val messages = if (this.message != null) listOf(this.message.toSimpleMessage()) else listOf()
    return ChatItemInfo(
        roomID = this.room.id.toInt(),
        usersIDs = usersIDs,
        unreadCount = this.chat.unread_count.toInt(),
        roomMessages = messages.toMutableList()
    )
}

fun MessageNew.toChatItemInfo(): ChatItemInfo {
    val usersIDs = mutableListOf<Int>()
    this.room.users.forEach {
        usersIDs.add(it.id.toInt())
    }
    return ChatItemInfo(
        roomID = this.room.id.toInt(),
        usersIDs = usersIDs,
        unreadCount = this.chat.unread_count.toInt(),
        roomMessages = mutableListOf(this.message.toSimpleMessage())
    )
}

val mockRoomChat = ChatItemInfo(
    roomID = 122,
    usersIDs = listOf(145, 176),
    unreadCount = 7,
    roomMessages = mutableListOf()
)

val mockRoomChats = listOf(
    mockRoomChat,
    mockRoomChat.copy(roomID = 123, unreadCount = 2, usersIDs = listOf(145, 164))
)

@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    deleteChat: (ChatDelete) -> Unit,
    navController: NavController,
    viewModel: ChatsViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val viewState = viewModel.viewStateProvider.viewState.collectAsState(ViewState.Display())

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.chats))
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("CreateChat")
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Создать чат")
            }
        }
    ) { innerPadding ->
        // Почему-то  снова не вызвается это место
        when (viewState.value) {
            is ViewState.StateLoading -> {
                LoadingScreen()
            }
            is ViewState.Error -> {
                ErrorScreen(
                    retryAction = { retryAction.invoke() },
                    errorText = (viewState.value as ViewState.Error).errorInfo
                )
            }
            is ViewState.StateNoItems -> {
                NoItemsView(message = "Чатов нет на данный момент", iconResID = null)
            }
            is ViewState.Display ->{
                val chats = viewModel.chatProviderImpl.chats.collectAsState(listOf())

                Log.d("ChatScreen", "Chats ${chats.value.size}")

                DisplayChats(modifier = Modifier.padding(innerPadding), chats =  chats.value, deleteChatAction = deleteChat, navController = navController)
            }
        }
    }

}

@Composable
fun DisplayChats(
    modifier: Modifier = Modifier,
    chats: List<ChatItemInfo>,
    deleteChatAction: (ChatDelete) -> Unit,
    navController: NavController, ) {
    if(chats.isNotEmpty()){
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
        ) {
            items(chats) { item ->
                ChatsItem(chatDetails = item,
                    OpenChat = { chatHistory->
                        retryAction = {
                            navController.navigate(Screen.Room.createRoute(roomID = chatHistory.roomId.toString()))
                        }
                        retryAction()
                    },
                    deleteChat = {
                        retryAction = { deleteChatAction(it) }
                        retryAction()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatsItem(
    modifier: Modifier = Modifier,
    chatDetails: ChatItemInfo,
    deleteChat: (ChatDelete) -> Unit,
    OpenChat: (ChatHistory) -> Unit,
) {
    Card(
        shape = Shapes.medium,
        onClick = { OpenChat(ChatHistory(lastId = null, limit = 10, roomId = chatDetails.roomID)) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Row {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "RoomID: ${chatDetails.roomID}")
                Text(
                    text = "Пользователи: ${chatDetails.usersIDs.joinToString()}",
                    modifier = Modifier.padding()
                )
                Text(text = "Количество не прочитаных сообщений: ${chatDetails.roomID}")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { deleteChat(ChatDelete(roomId = chatDetails.roomID)) },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }

        }

    }
}

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun ChatsScreenPreview() {
    ChatTheme {
        ChatsScreen(deleteChat = {}, navController = rememberNavController(), viewModel = viewModel())
    }
}