package com.pet.chat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.data.Attachment
import com.pet.chat.network.data.Message
import com.pet.chat.network.data.send.ChatRead
import com.pet.chat.network.data.send.DeleteMessage
import com.pet.chat.network.data.send.SendMessage
import com.pet.chat.ui.theme.LoveFinderTheme
import java.io.File

data class RoomMessage(
    val messageID: Int,
    val userID: String,
    val date: String,
    val text: String,
    val isOwn: Boolean = false,
)

fun Message.toRoomMessage(): RoomMessage {
    return RoomMessage(userID = user_id.toString(),
        date = created_at.toString(),
        text = text,
        isOwn = App.prefs?.userID == user_id.toInt(), messageID = this.id.toInt())
}

val mockAliceMessage =
    RoomMessage(userID = "Alice",
        date = "12.12.2021",
        text = "From Alice",
        isOwn = false,
        messageID = -1)
val mockBobMessage =
    RoomMessage(userID = "Bob",
        date = "12.12.2021",
        text = "From Bob",
        isOwn = true,
        messageID = -1)

val mockData: List<RoomMessage> = listOf(mockAliceMessage.copy(text = "Hi Bob"),
    mockAliceMessage.copy(text = "Hi Alice"),
    mockAliceMessage.copy(text = "How are you?"),
    mockBobMessage.copy(text = "I.m fine"),
    mockBobMessage,
    mockBobMessage,
    mockAliceMessage,
    mockBobMessage,
    mockAliceMessage)

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun ChatListPrewiew() {
    LoveFinderTheme {
        Chat(sendMessage = {},
            messages = mockData,
            navController = null,
            roomID = -1,
            clearChat = {},
            event = null,
            deleteMessage = { },
            eventChatRead = {},
            loadFileAction = {})
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Chat(
    event: EventFromServer?,
    modifier: Modifier = Modifier,
    sendMessage: (SendMessage) -> Unit,
    roomID: Int,
    clearChat: () -> Unit,
    messages: List<RoomMessage>,
    navController: NavController?,
    deleteMessage: (DeleteMessage) -> Unit,
    eventChatRead: (ChatRead) -> Unit,
    loadFileAction: (Attachment) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Room $roomID")
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { clearChat() }) {
                        Icon(Icons.Filled.ClearAll, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { innerPadding ->
        LoveFinderTheme {
            val (message, messageChange) = rememberSaveable { mutableStateOf("") }
            val listState = rememberLazyListState()
            val scaffoldState = rememberBottomSheetScaffoldState()

            BottomSheetAttachFile(scaffoldState = scaffoldState)

            if (listState.firstVisibleItemIndex >= messages.size - 1) {
                eventChatRead(ChatRead(roomId = roomID))
            }
            val sendAction = {
                sendMessage(SendMessage(roomId = roomID,
                    text = message,
                    attachmentId = null))
                messageChange("")
            }
            Column() {
                LazyColumn(modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .padding(innerPadding), state = listState) {
                    items(messages) { data ->
                        MessageItem(modifier = Modifier.padding(all = 4.dp),
                            message = data,
                            deleteMessage)
                    }
                }
                Column(modifier = Modifier.padding(all = 4.dp)) {
                    Row() {
                        TextField(modifier = Modifier,
                            value = message,
                            onValueChange = messageChange)
                        IconButton(onClick = { sendAction() }, enabled = message.isNotEmpty()) {
                            Icon(Icons.Filled.Send, contentDescription = "Send")
                        }
                        IconButton(onClick = {}, enabled = message.isNotEmpty()) {
                            Icon(Icons.Filled.Attachment, contentDescription = "AttachFile")
                        }

                    }

                    Button(onClick = { sendAction() },
                        modifier = modifier.fillMaxWidth(),
                        enabled = message.isNotEmpty()) {
                        Text(text = "Отправить")
                    }
                }

            }

        }
    }

}

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    message: RoomMessage,
    deleteMessage: (DeleteMessage) -> Unit,
) {
    val isMe = message.isOwn
    var expandedMenu by remember() { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        if (isMe) {
            Spacer(modifier = Modifier.weight(1f))
        }
        Card(modifier = modifier.fillMaxSize(0.7f)) {
            Column() {
                Row {
                    if (isMe) {
                        IconButton(onClick = { expandedMenu = !expandedMenu }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            Text(text = stringResource(id = R.string.delete),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        deleteMessage(DeleteMessage(message.messageID))
                                        expandedMenu = false
                                    }))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(text = message.userID,
                        Modifier
                            .padding(4.dp)
                            .align(Alignment.CenterVertically))
                }
                Text(text = message.text,
                    style = TextStyle.Default,
                    modifier = modifier
                        .padding(horizontal = 4.dp))
                Row {
                    if (isMe) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(text = message.date,
                        style = TextStyle.Default,
                        modifier = modifier.padding(horizontal = 4.dp))
                }

            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetAttachFile(modifier: Modifier = Modifier, scaffoldState: BottomSheetScaffoldState) {
    BottomSheetScaffold(
        sheetContent = {
        },
        scaffoldState = scaffoldState
    ) { innerPadding ->
        //Image or animation content
    }
}



