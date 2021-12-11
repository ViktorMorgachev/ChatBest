package com.pet.lovefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pet.lovefinder.network.ConnectionManager
import com.pet.lovefinder.network.Event
import com.pet.lovefinder.ui.theme.LoveFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoveFinderTheme {
                MyApp()
            }
        }

    }
}

@Composable
fun MyApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                }
            )
        }
    ) { innerPadding ->
        MainScreen()
    }
}

@Preview
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val event = ConnectionManager.events.collectAsState()
    val status by rememberSaveable { mutableStateOf(true) }
    if (status) {
        RegisteringScreen(modifier, event.value)
    }
}

@Composable
fun RegisteringScreen(modifier: Modifier = Modifier, value: Event) {
    val (id, idChange) = rememberSaveable { mutableStateOf("155") }
    val (token, tokenChange) = rememberSaveable { mutableStateOf("andr1") }
    Column(modifier = Modifier.padding(4.dp)) {
        TextField(value = id, onValueChange = idChange, Modifier.fillMaxWidth())
        TextField(value = token, onValueChange = tokenChange, Modifier.fillMaxWidth())
        Button(onClick = { ConnectionManager.auth(id.toInt(), token) }, Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Register")
        }
        Text(text = "Info: $value")
    }
}


