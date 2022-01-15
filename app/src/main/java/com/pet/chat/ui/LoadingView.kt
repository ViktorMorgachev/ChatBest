package com.pet.chat.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pet.chat.ui.theme.ChatTheme

@Preview(widthDp = 400, showSystemUi = true)
@Composable
fun LoadingViewPreview(){
    ChatTheme {
        LoadingView()
    }
}

@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
) {
    SideEffect {
        Log.d("Screen", "LoadingView")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(0.5f))
    }

}