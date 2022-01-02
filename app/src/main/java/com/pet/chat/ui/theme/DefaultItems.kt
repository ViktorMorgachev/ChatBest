package com.pet.chat.ui.theme

import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun snackBarHost(snackbarHostState: SnackbarHostState){
    SnackbarHost(snackbarHostState) { data ->
        Snackbar(
            snackbarData = data,
            backgroundColor = Color(0xFF004D40),
            contentColor = Color(0xFFB2DFDB),
            actionOnNewLine = true,
            actionColor = Color(0xFF009688)
        )
    }
}
