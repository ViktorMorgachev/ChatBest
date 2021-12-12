package com.pet.lovefinder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pet.lovefinder.App
import com.pet.lovefinder.network.data.Message
import com.pet.lovefinder.ui.Message as TestMessage
import com.pet.lovefinder.network.data.send.SendMessage
import com.pet.lovefinder.storage.LocalStorage
import com.pet.lovefinder.storage.Prefs
import com.pet.lovefinder.ui.theme.LoveFinderTheme
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun RoomChat(
    modifier: Modifier = Modifier,
    sendMessage: (SendMessage) -> Unit,
    navController: NavController,
) {
    val messages = LocalStorage.messages.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "RoomID ${2}")
                }
            )
        }
    ) { innerPadding ->
        LoveFinderTheme {
            val (message, messageChange) = rememberSaveable { mutableStateOf("") }
            Column() {
                LazyColumn(modifier = modifier.padding(4.dp)) {
                    items(messages.value) { item ->
                        if (item.user_id.toInt() != App.prefs?.userID) {
                            Row() {
                                Spacer(modifier = modifier.weight(1f))
                                RoomMessage(modifier = Modifier
                                    .padding(all = 8.dp).weight(1f), message = item)
                            }

                        } else {
                            Row() {
                                RoomMessage(modifier = Modifier.padding(all = 8.dp).weight(1f), message = item)
                                Spacer(modifier = modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = modifier.weight(1f))
                TextField(modifier = Modifier.fillMaxWidth(),
                    value = message,
                    onValueChange = messageChange,
                    trailingIcon = { Icon(Icons.Filled.Send, contentDescription = "Отправить") })
                Button(onClick = {
                    sendMessage(SendMessage(roomId = 2,
                        text = message,
                        attachmentId = null))
                }, modifier = modifier.fillMaxWidth()) {
                    Text(text = "Отправить")
                }
            }

        }
    }


}

@Composable
fun RoomMessage(modifier: Modifier = Modifier, message: Message) {
    Card() {
        Column() {
            Text(text = message.text, Modifier.padding(4.dp))
            Row() {
                Spacer(modifier = modifier.weight(1f))
                Text(text = message.updated_at ?: "", style = TextStyle.Default)
            }

        }
    }
}

data class Message(val text: String, val isMe: Boolean)


