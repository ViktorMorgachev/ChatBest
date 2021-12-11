package com.pet.lovefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pet.lovefinder.network.Event
import com.pet.lovefinder.network.data.AuthData
import com.pet.lovefinder.ui.ChatViewModel
import com.pet.lovefinder.ui.theme.LoveFinderTheme

class MainActivity : ComponentActivity() {

    val eventViewModel by viewModels<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoveFinderTheme {
                MyApp(eventViewModel)
            }
        }

    }
}

@Composable
fun MyApp(viewModel: ChatViewModel = ChatViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                }
            )
        }
    ) { innerPadding ->
        MainScreen(viewModel = viewModel)
    }
}

@Preview
@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: ChatViewModel = ChatViewModel()) {
    val event = viewModel.events.collectAsState()
    RegisteringScreen(modifier, event.value) {
        viewModel.login(it)
    }
    /*when (event.value) {
        is Event.Autorization -> {
            if ((event.value as Event.Autorization).success) {
                ShowChatsScreen(modifier, viewModel, event.value)
            } else {
                RegisteringScreen(modifier, event.value) {
                    viewModel.login(it)
                }
            }
        }
        is Event.Default -> RegisteringScreen(modifier, event.value) {
            viewModel.login(it)
        }

    }*/
}

@Composable
fun ShowChatsScreen(modifier: Modifier, viewModel: ChatViewModel, event: Event) {

    Column() {
        LazyColumn(Modifier.padding(4.dp)) {
            item {

            }
        }
        Button(onClick = { viewModel.createDialog() }) {

        }
    }

}

@Composable
fun RegisteringScreen(
    modifier: Modifier = Modifier,
    value: Event,
    onAuthEvent: (AuthData) -> Unit,
) {
    val (id, idChange) = rememberSaveable { mutableStateOf("155") }
    val (token, tokenChange) = rememberSaveable { mutableStateOf("andr1") }
    Column(modifier = Modifier.padding(4.dp)) {
        TextField(value = id, onValueChange = idChange, Modifier.fillMaxWidth())
        TextField(value = token, onValueChange = tokenChange, Modifier.fillMaxWidth())
        Button(onClick = { onAuthEvent(AuthData(id.toInt(), token)) },
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)) {
            Text(text = "Register")
        }
        Text(text = "Info: $value")
    }
}


