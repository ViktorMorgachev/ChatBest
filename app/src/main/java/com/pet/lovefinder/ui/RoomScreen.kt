package com.pet.lovefinder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pet.lovefinder.App
import com.pet.lovefinder.network.data.Message
import com.pet.lovefinder.network.data.send.SendMessage
import com.pet.lovefinder.ui.theme.LoveFinderTheme

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
        Chat(sendMessage = {}, messages = mockData, navController = null, roomID = -1)
    }
}

@Composable
fun Chat(
    modifier: Modifier = Modifier,
    sendMessage: (SendMessage) -> Unit,
    roomID: Int,
    messages: List<RoomMessage>,
    navController: NavController?,
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
                }
            )
        }
    ) { innerPadding ->
        LoveFinderTheme {
            var (message, messageChange) = rememberSaveable { mutableStateOf("") }
            Column() {
                LazyColumn(modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .padding(innerPadding)) {
                    items(messages) { data ->
                        MessageItem(modifier = Modifier.padding(all = 4.dp), message = data)
                    }
                }
                Column(modifier = Modifier.padding(all = 4.dp)) {
                    TextField(modifier = Modifier.fillMaxWidth(),
                        value = message,
                        onValueChange = messageChange,
                        trailingIcon = {
                            Icon(Icons.Filled.Send,
                                contentDescription = "Отправить")
                        })
                    Button(onClick = {
                        sendMessage(SendMessage(roomId = roomID,
                            text = message,
                            attachmentId = null))
                        messageChange("")
                    }, modifier = modifier.fillMaxWidth()) {
                        Text(text = "Отправить")
                    }
                }

            }

        }
    }

}

@Composable
fun MessageItem(modifier: Modifier = Modifier, message: RoomMessage) {
    val isMe = message.isOwn
    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        if (isMe) {
            Spacer(modifier = Modifier.weight(1f))
        }
        Card(modifier = modifier.fillMaxSize(0.5f)) {
            Column() {
                Row {
                    if (isMe) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(text = message.userID, Modifier.padding(4.dp))
                }
                Text(text = message.text,
                    style = TextStyle.Default,
                    modifier = modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterHorizontally))
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



