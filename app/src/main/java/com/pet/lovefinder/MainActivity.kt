package com.pet.lovefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Preview(showBackground = true, widthDp = 320)
@Composable
fun MyAppPreview() {
    LoveFinderTheme {
        MyApp()
    }
}

@Composable
fun MyApp() {
    val shouldShowOnboarding = rememberSaveable { mutableStateOf(true) }
    if (shouldShowOnboarding.value) {
        OnboardingScreen { shouldShowOnboarding.value = !shouldShowOnboarding.value }
    } else Greetings()
}

@Composable
private fun Greetings(names: List<String> = List(10000) { "$it" }) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = names){
            Greeting(name = it)
        }
    }
}

@Composable
private fun Greeting(name: String) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    val extraPading = if (expanded.value) 48.dp else 0.dp
    Surface(color = MaterialTheme.colors.primary, modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 8.dp)) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPading)) {
                Text(text = "Element")
                Text(text = name)
            }
            OutlinedButton(onClick = {
                expanded.value = !expanded.value
            }) {
                Text(text = if (!expanded.value) "Show more" else "Show less")
            }
        }

    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    LoveFinderTheme {
        OnboardingScreen {}
    }
}