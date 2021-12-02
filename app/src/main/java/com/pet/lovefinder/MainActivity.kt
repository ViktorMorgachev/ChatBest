package com.pet.lovefinder

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "DefaultPreviewDark")
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

@Preview(uiMode = UI_MODE_NIGHT_YES, name = "DefaultPreviewDark")
//@Preview(uiMode = UI_MODE_NIGHT_NO, name = "DefaultPreviewLight")
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun Greetings(names: List<String> = List(10000) { "$it" }) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = names) {
            Greeting(name = it)
        }
    }
}

@Composable
private fun Greeting(name: String) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    val extraPading by animateDpAsState(
        targetValue = if (expanded.value) 48.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessVeryLow))
    Surface(color = MaterialTheme.colors.primary, modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 8.dp)) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPading.coerceAtLeast(0.dp))) {
                Text(text = "Element")
                Text(text = name, style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.ExtraBold))
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
