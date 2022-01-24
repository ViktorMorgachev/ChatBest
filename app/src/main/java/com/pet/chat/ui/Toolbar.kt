package com.pet.chat.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Box(contentAlignment = Alignment.Center) {
        content()
    }
}

val defaultMockToolbar = Toolbar(modifier = Modifier
    .fillMaxWidth()
    .height(46.dp).padding(4.dp))

@Preview(widthDp = 400, showSystemUi = false)
@Composable
fun ToolbarPreview() {
    ChatTheme {
        defaultMockToolbar.invoke()
    }

}

data class Toolbar(val modifier: Modifier = Modifier, val text: String = "",val leftActions: List<@Composable () -> Unit> = listOf(), val rightActions: List<@Composable () -> Unit> = listOf()){
   @Composable
    fun invoke(){
       ToolbarView(modifier= modifier, text = text, leftActions = leftActions, rightActions = rightActions)
   }
}

@Composable
fun ToolbarView(
    modifier: Modifier = Modifier,
    text: String = "",
    leftActions: List<@Composable () -> Unit>,
    rightActions: List<@Composable () -> Unit>,
) {
    Row(modifier = modifier.height(46.dp)) {
        leftActions.forEach { action->
            ToolbarAction(action)
        }
        if(leftActions.isEmpty()){
            Box(modifier = Modifier.weight(1f))
        }
        if (!text.isNullOrEmpty()) {
            Text(
                text = text, modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .weight(1f).padding(8.dp)
            )
        }
        rightActions.forEach { action ->
            ToolbarAction(action)
        }
        if(rightActions.isEmpty()){
            Box(modifier = Modifier.weight(1f))
        }

    }
}