package com.pet.lovefinder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pet.lovefinder.App
import com.pet.lovefinder.helpers.isOwn
import com.pet.lovefinder.network.data.Message
import com.pet.lovefinder.network.data.send.SendMessage
import com.pet.lovefinder.storage.LocalStorage
import com.pet.lovefinder.storage.Prefs
import com.pet.lovefinder.ui.theme.LoveFinderTheme

@Composable
fun RoomChat(
    modifier: Modifier = Modifier,
    sendMessage: (SendMessage) -> Unit,
    navController: NavController,
) {
    val messages = LocalStorage.messages.collectAsState()
    val roomID by rememberSaveable { mutableStateOf(2) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "RoomID ${roomID}")
                }
            )
        }
    ) { innerPadding ->
        LoveFinderTheme {
            val (message, messageChange) = rememberSaveable { mutableStateOf("") }
            Column() {
                LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                    items(messages.value) { item ->
                        RoomMessage(modifier = Modifier
                            .padding(all = 8.dp), message = item)
                    }
                }
                Spacer(modifier = modifier.weight(1f))
                TextField(modifier = Modifier.fillMaxWidth(),
                    value = message,
                    onValueChange = messageChange,
                    trailingIcon = { Icon(Icons.Filled.Send, contentDescription = "Отправить") })
                Button(onClick = {
                    sendMessage(SendMessage(roomId = roomID,
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
    if (message.isOwn()){
        Row() {
            Card(modifier = Modifier.fillMaxWidth(0.5f)) {
                Column() {
                    Text(text = message.text, Modifier.padding(4.dp))
                    Row() {
                        Spacer(modifier = modifier.weight(1f))
                        Text(text = message.updated_at ?: "", style = TextStyle.Default)
                    }

                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        Row() {
            Spacer(modifier = Modifier.weight(1f))
            Card(modifier = Modifier.fillMaxWidth(0.5f)) {
                Column() {
                    Text(text = message.text, Modifier.padding(4.dp))
                    Row() {
                        Spacer(modifier = modifier.weight(1f))
                        Text(text = message.updated_at ?: "", style = TextStyle.Default)
                    }

                }
            }

        }
    }


}



