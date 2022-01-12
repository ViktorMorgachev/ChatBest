package com.pet.chat.ui

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pet.chat.network.EventFromServer


@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun ErrorViewPreview(){
    ErrorView(retryAction = { }, errorText = "Ошибка")
}

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    retryAction: () -> Unit,
    errorText: String,
) {
    SideEffect {
        Log.d("Screen", "ErrorViewScreen")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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