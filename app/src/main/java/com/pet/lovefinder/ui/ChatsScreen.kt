package com.pet.lovefinder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pet.lovefinder.R
import com.pet.lovefinder.network.EventFromServer
import com.pet.lovefinder.network.data.base.ChatDetails
import com.pet.lovefinder.network.data.send.ChatDelete
import com.pet.lovefinder.network.data.send.ChatHistory
import com.pet.lovefinder.storage.LocalStorage
import com.pet.lovefinder.ui.theme.Shapes

@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    value: EventFromServer,
    deleteChat: (ChatDelete) -> Unit,
    openChat: (ChatHistory) -> Unit,
    navController: NavController,
) {
    val chats = LocalStorage.chats.collectAsState()
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
            FloatingActionButton(onClick = { navController.navigate("CreateChat") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Создать чат")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(chats.value) { item ->
                ChatItem(chatDetails = item,
                    OpenChat = { openChat(it) },
                    deleteChat = { deleteChat(it) })
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    chatDetails: ChatDetails,
    deleteChat: (ChatDelete) -> Unit,
    OpenChat: (ChatHistory) -> Unit,
) {
    Card(shape = Shapes.medium,
        onClick = {
            OpenChat(ChatHistory(lastId = null,
                limit = 10,
                roomId = chatDetails.roomID))
        }, modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
        Column() {
            Row() {
                Text(text = "RoomID: ${chatDetails.roomID}", modifier = Modifier.weight(1f))
                Button(onClick = { deleteChat(ChatDelete(roomId = chatDetails.roomID)) },
                    modifier = Modifier.padding(4.dp)) {
                    Text(text = "Удалить")
                }
            }
            Text(text = "Пользователи: ${chatDetails.users}")
        }
    }
}