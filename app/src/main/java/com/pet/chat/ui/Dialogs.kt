package com.pet.chat.ui

import android.net.Uri
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.ui.unit.sp

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.pet.chat.network.data.Message
import com.pet.chat.ui.theme.LoveFinderTheme
import java.io.File

@Composable
fun FilePreviewDialog(
    fileUri: Uri?,
    applyMessage: (message: String, fileUri: Uri) -> Unit,
    openDialog: MutableState<Boolean>,
) {
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        title = { Text(text = "Тестовый диалог") },
        text = { Text("Тестовый диалог для открытия") },
        buttons = {
            Button(
                onClick = { openDialog.value = false }
            ) {
                Text("OK", fontSize = 22.sp)
            }
        }
    )
}

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun FilePreviewDialogPreview() {
    LoveFinderTheme {
        FilePreviewDialog(fileUri = null, applyMessage = {_, _->}, openDialog = remember { mutableStateOf(false) })
    }

}