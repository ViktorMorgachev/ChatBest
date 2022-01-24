package com.pet.chat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


var toolbarBackground = Color.Unspecified
var messageOwnBackGround = Color.Unspecified
var messageBackGround = Color.Unspecified
var contentColor = Color.Unspecified


private val DarkColorPalette = darkColors(
    surface = Blue,
    onSurface =  Color.Black,
    primary = Blue,
    onPrimary = Color.Black
)

private val LightColorPalette = lightColors(
    surface = Blue,
    onSurface = Color.White,
    primary = LightBlue,
    onPrimary = Color.White
)



@Composable
fun ChatTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    if (darkTheme){
        toolbarBackground = Color(40, 43, 46, 1)
        messageOwnBackGround = Color(31, 57, 71)
        messageBackGround = Color(54, 54, 54)
        contentColor = Color.White
    } else {
        toolbarBackground = Color(255, 255, 255, 1)
        messageOwnBackGround = Color(183, 229, 255)
        messageBackGround = Color(250, 250, 250)
        contentColor = Color.Black
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}