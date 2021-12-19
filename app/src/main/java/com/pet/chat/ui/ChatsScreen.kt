package com.pet.chat.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.network.data.Dialog
import com.pet.chat.network.data.receive.MessageNew
import com.pet.chat.network.data.send.ChatDelete
import com.pet.chat.network.data.send.ChatHistory
import com.pet.chat.ui.main.ChatViewModel
import com.pet.chat.ui.theme.ChatTheme
import com.pet.chat.ui.theme.Shapes

data class ChatItemInfo(
    val roomID: Int,
    val usersIDs: List<Int>,
    var unreadCount: Int,
    var roomMessages: List<RoomMessage> = listOf(),
)

fun Dialog.toChatItemInfo(): ChatItemInfo {
    val usersIDs = mutableListOf<Int>()
    this.room.users.forEach {
        usersIDs.add(it.id.toInt())
    }
    val messages = if (this.message != null) listOf(this.message.toRoomMessage()) else listOf()
    return ChatItemInfo(roomID = this.room.id.toInt(),
        usersIDs = usersIDs,
        unreadCount = this.chat.unread_count.toInt(),
        roomMessages = messages.toMutableList())
}

fun MessageNew.toChatItemInfo(): ChatItemInfo {
    val usersIDs = mutableListOf<Int>()
    this.room.users.forEach {
        usersIDs.add(it.id.toInt())
    }
    return ChatItemInfo(roomID = this.room.id.toInt(),
        usersIDs = usersIDs,
        unreadCount = this.chat.unread_count.toInt(),
        roomMessages = mutableListOf(this.message.toRoomMessage()))
}

val mockRoomChat = ChatItemInfo(roomID = 122,
    usersIDs = listOf(145, 176),
    unreadCount = 7,
    roomMessages = mutableListOf())

val mockRoomChats = listOf(
    mockRoomChat,
    mockRoomChat.copy(roomID = 123, unreadCount = 2, usersIDs = listOf(145, 164))
)

@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    deleteChat: (ChatDelete) -> Unit,
    openChat: (ChatHistory) -> Unit,
    navController: NavController?,
    viewModel: ChatViewModel,
) {
    val chats = viewModel.chats.collectAsState()
    Log.d("ChatScreen", "Chats ${chats.value.size}")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.chats))
                }
            )
        },

        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { navController?.navigate("CreateChat") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Создать чат")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = modifier
            .fillMaxWidth()
            .padding(innerPadding)) {
            items(chats.value) { item ->
                ChatsItem(chatDetails = item,
                    OpenChat = {
                        openChat(it)
                    },
                    deleteChat = { deleteChat(it) })
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
    Card(shape = Shapes.medium,
        onClick = { OpenChat(ChatHistory(lastId = null, limit = 10, roomId = chatDetails.roomID)) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Row {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "RoomID: ${chatDetails.roomID}")
                Text(text = "Пользователи: ${chatDetails.usersIDs.joinToString()}",
                    modifier = Modifier.padding())
                Text(text = "Количество не прочитаных сообщений: ${chatDetails.roomID}")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { deleteChat(ChatDelete(roomId = chatDetails.roomID)) },
                modifier = Modifier.padding(4.dp)) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }

        }

    }
}

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun ChatsScreenPreview() {
    ChatTheme {
        ChatsScreen(deleteChat = {}, openChat = {}, navController = null, viewModel = viewModel())
    }
}