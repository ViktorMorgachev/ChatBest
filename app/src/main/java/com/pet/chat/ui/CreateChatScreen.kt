package com.pet.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.data.send.ChatStart
import com.pet.chat.ui.theme.ChatTheme

@Preview
@Composable
fun CreateChatScreenPreview() {
    ChatTheme {
        CreateChatScreenPreview()
    }
}

@Composable
fun CreateChatScreen(
    modifier: Modifier = Modifier,
    createChat: (ChatStart) -> Unit,
    navController: NavController,
    toolbar: Toolbar?
) {
    Scaffold(
    ) { innerPadding ->
        val (userID, userIDChange) = rememberSaveable { mutableStateOf("156") }
        val (text, textChange) = rememberSaveable { mutableStateOf("Привет $userID") }
        Column(Modifier.fillMaxWidth()) {
            toolbar?.invoke()
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(value = userID, onValueChange = userIDChange, Modifier.fillMaxWidth())
            TextField(value = text, onValueChange = textChange, Modifier.fillMaxWidth())
            Button(onClick = {
                createChat(ChatStart(userID.toInt(), text))
                navController.navigateUp()
            },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)) {
                Text(text = "Create")
            }
        }
    }
}

@Composable
fun CreateChatScreenPreview(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.createChat))
                }
            )
        }
    ) { innerPadding ->
        val (userID, userIDChange) = rememberSaveable { mutableStateOf("156") }
        val (text, textChange) = rememberSaveable { mutableStateOf("Привет $userID") }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(value = userID, onValueChange = userIDChange, Modifier.fillMaxWidth())
            TextField(value = text, onValueChange = textChange, Modifier.fillMaxWidth())
            Button(onClick = { /*onAuthEvent(AuthData(id.toInt(), token))*/ },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)) {
                Text(text = "Create")
            }
        }
    }

}