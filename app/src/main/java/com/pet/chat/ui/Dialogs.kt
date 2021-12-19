package com.pet.chat.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pet.chat.App
import com.pet.chat.events.InternalEvent
import com.pet.chat.ui.main.ChatViewModel
import com.pet.chat.ui.theme.ChatTheme

@Composable
fun FilePreviewDialog(
    fileUri: Uri?,
    filePath: String?,
    applyMessage: (message: String, fileUri: Uri?, filePath: String?) -> Unit,
    openDialog: (Boolean) -> Unit
) {
    val (message, messageChange) = rememberSaveable { mutableStateOf("") }
    Dialog(
        onDismissRequest = {
            App.states?.cameraFilePath = ""
            openDialog(false)
        }
    ){
        Column(modifier = Modifier.padding(4.dp)) {
            val bitmap = BitmapFactory.decodeFile(filePath)
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)) {
                Image(bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Photo for sending", modifier = Modifier.padding(4.dp) )
                /*  TextField(value = message,
                      onValueChange = messageChange)*/
                Row {
                    Button(
                        onClick = {
                            openDialog(false)
                            applyMessage(message, fileUri, filePath)
                        },
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text("Отправить", fontSize = 22.sp)
                    }
                    Button(
                        onClick = {
                            openDialog(false)
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
        FilePreviewDialog(fileUri = null,
            applyMessage = { _, _, _ -> },
            openDialog = {},
            filePath = null)
    }

}