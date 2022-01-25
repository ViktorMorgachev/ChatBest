package com.pet.chat.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pet.chat.App
import com.pet.chat.network.data.base.File
import com.pet.chat.ui.screens.chat.State
import com.pet.chat.ui.theme.ChatTheme

@Composable
fun FilePreviewDialog(
    file: File?,
    applyMessage: (String) -> Unit,
    closeDialog: () -> Unit,
) {
    val (message, messageChange) = rememberSaveable { mutableStateOf("") }
    Dialog(
        onDismissRequest = {
            MainChatModule.chatsPrefs?.cameraFilePath = ""
            closeDialog.invoke()
            // viewModel.postInternalAction(InternalEvent.OpenFilePreview(fileUri, filePath, false))
        }
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            val bitmap = BitmapFactory.decodeFile(file!!.filePath!!)
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)) {
                Image(bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Photo for sending", modifier = Modifier.padding(4.dp))
                TextField(value = message,
                    onValueChange = messageChange)
                Row {
                    Button(
                        onClick = {
                            closeDialog.invoke()
                            applyMessage(message)
                        },
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text("Отправить", fontSize = 22.sp)
                    }
                    Button(
                        onClick = {
                            closeDialog.invoke()
                        },
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text("Отмена", fontSize = 22.sp)
                    }
                }
            }
        }

    }
}

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun FilePreviewDialogPreview() {
    ChatTheme {
        FilePreviewDialog(
            file = null,
            applyMessage = { },
            closeDialog = {})
    }

}