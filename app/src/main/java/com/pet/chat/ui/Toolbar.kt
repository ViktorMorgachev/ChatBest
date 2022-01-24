package com.pet.chat.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pet.chat.ui.theme.ChatTheme

@Composable
fun alert() {
    AlertDialog(
        title = {
            Text(text = "Test")
        },
        text = {
            Text("Test")
        },
        onDismissRequest = {

        },
        buttons = {
            Button(onClick = { }) {
                Text("test")
            }
        }

    )
}

@Composable
fun ToolbarAction(content: @Composable () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
        content()
    }
}


@Preview(widthDp = 400, showSystemUi = false)
@Composable
fun ToolbarPreview() {
    ChatTheme() {
        ToolbarView(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            text = "Toolbar",
            onBackPressed = { },
            actions = listOf {
                IconButton(onClick = {  }) {
                    Icon(Icons.Filled.ClearAll, contentDescription = "Clear")
                }
            }
        )
    }

}

@Composable
fun ToolbarView(
    modifier: Modifier = Modifier,
    text: String = "",
    onBackPressed: (() -> Unit)? = null,
    actions: List<@Composable () -> Unit>
) {
    Row() {
        onBackPressed?.let {
            IconButton(onClick = { onBackPressed() }, modifier = Modifier.align(alignment = Alignment.CenterVertically)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        if (!text.isNullOrEmpty()) {
            Text(
                text = text, modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1f)
            )
        }
        actions.forEach { action ->
            ToolbarAction(action)
        }

    }
}