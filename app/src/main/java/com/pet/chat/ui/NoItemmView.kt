package com.pet.chat.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoItemsView(message: String, iconResID: Int?, modifier: Modifier = Modifier) {
    SideEffect {
        Log.d("Screen", "NoItemsViewScreen")
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(text = message, Modifier.padding(4.dp))
    }
}