package com.pet.chat.ui.screens.autorization

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pet.chat.App
import com.pet.chat.R
import com.pet.chat.network.EventToServer
import com.pet.chat.network.data.ViewState
import com.pet.chat.network.data.send.UserAuth
import com.pet.chat.ui.ErrorScreen
import com.pet.chat.ui.LoadingScreen
import com.pet.chat.ui.Screen
import com.pet.chat.ui.theme.ChatTheme
import com.pet.chat.ui.theme.snackBarHost

val tagForState = "AutorizationScreen"

@Composable
fun AutorizationScreen(
    modifier: Modifier = Modifier,
    onAuthEvent: (UserAuth) -> Unit,
    viewModel: AutorizationViewModel,
    navController: NavController
) {
    val viewState = viewModel.viewStateProvider.viewState.collectAsState(ViewState.Display())

    LaunchedEffect(key1 = Unit, block = {
        if (App.prefs?.identified() == true) {
            onAuthEvent(UserAuth(App.prefs!!.userID, App.prefs!!.userToken))
        }
    })

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
            viewModel.authorize(EventToServer.AuthEvent(UserAuth(id.toInt(), token)))
        }
        when(viewState.value){
            is ViewState.StateLoading ->{
                LoadingScreen()
            }
            is ViewState.Error ->{
                ErrorScreen(retryAction = { authClick() }, errorText = (viewState.value as ViewState.Error).errorInfo)
            }
            is ViewState.Display -> {
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
                        onClick = {authClick()},
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.auth))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            is ViewState.Success->{
                navController.navigate(Screen.Chats.route)
            }
            else -> {
                println("$tagForState Unsupported state ${viewState.value}")
            }
        }
    }
}


@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun AutorizationScreenPrewiew() {
    ChatTheme {
        AutorizationScreen(onAuthEvent = {}, viewModel = hiltViewModel(), navController = rememberNavController())
    }
}