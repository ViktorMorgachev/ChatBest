package com.pet.lovefinder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.pet.lovefinder.R
import com.pet.lovefinder.network.EventFromServer
import com.pet.lovefinder.network.data.send.UserAuth
import com.pet.lovefinder.ui.theme.LoveFinderTheme

@Composable
fun AutorizationScreen(
    modifier: Modifier = Modifier,
    value: EventFromServer,
    onAuthEvent: (UserAuth) -> Unit,
    navController: NavController?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                }
            )
        }
    ) { innerPadding ->
        val (id, idChange) = rememberSaveable { mutableStateOf("155") }
        val (token, tokenChange) = rememberSaveable { mutableStateOf("andr1") }
        Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(value = id, onValueChange = idChange, Modifier.fillMaxWidth())
            TextField(value = token, onValueChange = tokenChange, Modifier.fillMaxWidth())
            Button(onClick = { onAuthEvent(UserAuth(id.toInt(), token)) },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)) {
                Text(text = "Register")
            }
            Text(text = "Info: $value")
        }
    }

}

@Preview
@Composable
fun AutorizationScreenPrewiew(){
    LoveFinderTheme {
        AutorizationScreen(value = EventFromServer.Debug("Test"), onAuthEvent = {}, navController = null)
    }
}