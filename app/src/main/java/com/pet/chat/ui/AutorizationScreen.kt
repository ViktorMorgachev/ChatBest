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
import com.pet.chat.R
import com.pet.chat.network.data.send.UserAuth
import com.pet.chat.ui.theme.ChatTheme

@Composable
fun AutorizationScreen(
    modifier: Modifier = Modifier,
    onAuthEvent: (UserAuth) -> Unit,
    navController: NavController?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.autorization))
                }
            )
        }
    ) { innerPadding ->
        val (id, idChange) = rememberSaveable { mutableStateOf("155") }
        val (token, tokenChange) = rememberSaveable { mutableStateOf("andr1") }
        Column(modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .fillMaxHeight()) {
            Spacer(modifier = Modifier.weight(1f))
            val textFieldModifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .align(Alignment.CenterHorizontally)
            TextField(value = id, onValueChange = idChange, modifier = textFieldModifier)
            TextField(value = token, onValueChange = tokenChange, modifier = textFieldModifier)
            Button(onClick = {
                onAuthEvent(UserAuth(id.toInt(), token))
                navController?.navigate("Chats")
            },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)) {
                Text(text = stringResource(id = R.string.auth))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun AutorizationScreenPrewiew() {
    ChatTheme {
        AutorizationScreen(onAuthEvent = {}, navController = null)
    }
}