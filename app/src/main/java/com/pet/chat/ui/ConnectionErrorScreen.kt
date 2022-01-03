package com.pet.chat.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pet.chat.network.EventFromServer

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    retryAction: () -> Unit,
    errorText: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp), contentAlignment = Alignment.Center
    ) {
        Column() {
            if (errorText.isNotEmpty()) {
                Text(text = errorText, modifier = Modifier)
            }
            Button(onClick = {
                retryAction.invoke()
            }) {
                Text(text = "Повторить")
            }
        }

    }
}