package com.pet.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.data.send.UserAuth
import com.pet.chat.ui.main.ChatViewModel
import com.pet.chat.ui.theme.ChatTheme
import com.pet.chat.ui.theme.snackBarHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun AutorizationScreen(
    modifier: Modifier = Modifier,
    onAuthEvent: (UserAuth) -> Unit,
    viewModel: ChatViewModel?
) {

    if (App.prefs?.identified() == true) {
        onAuthEvent(UserAuth(App.prefs!!.userID, App.prefs!!.userToken))
    }

    val eventsFromServer = viewModel?.events?.collectAsState()
    val (showLoadingScreen, showLoadingScreenChange) = remember { mutableStateOf(false) }




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.autorization))
                }
            )
        }, snackbarHost = {
            snackBarHost(snackbarHostState = it)
        }
    ) { innerPadding ->
        val (id, idChange) = rememberSaveable { mutableStateOf("155") }
        val (token, tokenChange) = rememberSaveable { mutableStateOf("andr1") }

        val authClick = {
            onAuthEvent(UserAuth(id.toInt(), token))
            showLoadingScreenChange.invoke(true)
        }
        when {
            showLoadingScreen -> {
                loadingScreen()
            }

            eventsFromServer?.value == EventFromServer.ConnectionError() -> {
                connectionError(
                    retryAction = { authClick.invoke() },
                    errorText = (eventsFromServer.value as EventFromServer.ConnectionError).data
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    val textFieldModifier = Modifier
                        .fillMaxWidth(fraction = 0.5f)
                        .align(Alignment.CenterHorizontally)
                    TextField(value = id, onValueChange = idChange, modifier = textFieldModifier)
                    TextField(value = token, onValueChange = tokenChange, modifier = textFieldModifier)
                    Button(
                        onClick = authClick,
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.auth))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun AutorizationScreenPrewiew() {
    ChatTheme {
        AutorizationScreen(onAuthEvent = {}, viewModel = null)
    }
}