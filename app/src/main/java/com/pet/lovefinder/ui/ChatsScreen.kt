package com.pet.lovefinder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.pet.lovefinder.R
import com.pet.lovefinder.network.EventFromServer
import com.pet.lovefinder.network.data.base.ChatDetails
import com.pet.lovefinder.network.data.send.ChatHistory
import com.pet.lovefinder.storage.LocalStorage
import com.pet.lovefinder.ui.theme.Shapes

@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    value: EventFromServer,
    deleteChat: () -> Unit,
    openChat: (ChatHistory) -> Unit,
    navController: NavController,
) {
    val chats = LocalStorage.chats.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
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
                ChatItem(chatDetails = item, OpenChat = { openChat(it) })
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    chatDetails: ChatDetails,
    OpenChat: (ChatHistory) -> Unit,
) {
    Card(shape = Shapes.medium,
        onClick = {
            OpenChat(ChatHistory(lastId = null,
                limit = 10,
                roomId = chatDetails.roomID))
        }) {
        Column() {
            Row() {
                Text(text = "RoomID: ${chatDetails.roomID}")
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Delete")
                }
            }
            Text(text = "Users: ssdfsdf asdasdasd asdasd")
        }
    }
}