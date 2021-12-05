package com.pet.lovefinder

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun MyApp() {
    var favouriteActionState by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "LayoutsCodelab")
                }, actions = {
                    IconButton(onClick = { favouriteActionState = !favouriteActionState }) {
                        Icon(imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favourte",
                            tint = if (favouriteActionState) Color.Red else Color.White)
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Filled.Send, contentDescription = "Send")
                    }
                }
            )
        }, bottomBar = {
            BottomAppBar(modifier = Modifier) {
                val bottomItems = listOf(Icons.Filled.Chat to "Chat",
                    Icons.Filled.Favorite to "Favorite",
                    Icons.Filled.Settings to "Settings")
                BottomItems(items = bottomItems, modifier = Modifier.weight(1f, true))
               /* IconButton(onClick = {  }) { Icon(Icons.Filled.Menu, contentDescription = "Меню")}
                Spacer(Modifier.weight(1f, true))
                IconButton(onClick = {  }) { Icon(Icons.Filled.Search, contentDescription = "Поиск")}*/
            }

        }
    ) { innerPadding ->
        val shouldShowOnboarding = rememberSaveable { mutableStateOf(true) }
        if (shouldShowOnboarding.value) {
            OnboardingScreen(modifier = Modifier.padding(innerPadding)) {
                shouldShowOnboarding.value = !shouldShowOnboarding.value
            }
        } else Greetings(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp))
    }

}

@Preview
@Composable
private fun BottomItem(
    modifier: Modifier = Modifier,
    pair: Pair<ImageVector, String> = Icons.Filled.Chat to "Chat",
) {
    Column() {
        Icon(imageVector = pair.first, contentDescription = pair.second)
        Text(text = pair.second)
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, name = "DefaultPreviewDark")
//@Preview(uiMode = UI_MODE_NIGHT_NO, name = "DefaultPreviewLight")
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun Greetings(modifier: Modifier = Modifier, names: List<String> = List(10000) { "$it" }) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = names) {
            Greeting(name = it)
        }
    }
}

@Composable
private fun BottomItems(items: List<Pair<ImageVector, String>>, modifier: Modifier = Modifier) {
    Spacer(modifier = modifier)
    items.forEachIndexed { index, pair ->
        BottomItem(pair = pair)
        Spacer(modifier = modifier)
    }
}

@Composable
private fun Greeting(modifier: Modifier = Modifier, name: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val contentDescription =
        if (expanded) stringResource(R.string.show_less) else stringResource(R.string.show_more)
    val imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore

    Card(backgroundColor = MaterialTheme.colors.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Row(
            modifier = modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = "Hello, ")
                Text(
                    text = name,
                    style = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                if (expanded) {
                    Text(
                        text = ("Composem ipsum color sit lazy, " + "padding theme elit, sed do bouncy. ").repeat(
                            4),
                    )
                }
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = imageVector, contentDescription = contentDescription)
            }
        }

    }
}

@Composable
fun OnboardingScreen(modifier: Modifier = Modifier, onContinueClicked: () -> Unit) {
    Surface {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview
@Composable
fun LayoutsCodelab() {

}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Hi there!")
        Text(text = "Thanks for going through the Layouts codelab")
    }
}
