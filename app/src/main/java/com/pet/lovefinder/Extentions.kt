package com.pet.lovefinder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pet.lovefinder.ui.theme.LoveFinderTheme

inline infix fun <reified T> Int.between(list: List<T>): Boolean {
    return this == 0 || this == list.size
}

fun Modifier.firstBaseLineTop(firstBaseLineTop: Dp) = this.then(layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    // Check the composable has a first baseline
    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]
    // Height of the composable with padding - first baseline
    val placeableY = firstBaseLineTop.roundToPx() - firstBaseline
    val height = placeable.height + placeableY
    layout(placeable.width, height) {
        placeable.placeRelative(0, placeableY)
    }
})

@Composable
fun MyOwnColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map {
            // Measure each child
                measure ->
            measure.measure(constraints)

        }

        // Track the y co-ord we have placed children up to
        var yPosition = 0

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height

            }

        }
    }
}

@Preview
@Composable
fun FirstBaseLineTopTest() {
    LoveFinderTheme() {
        Column() {
            Text(text = "Test text", modifier = Modifier.firstBaseLineTop(32.dp))
            Text(text = "Text text 2", modifier = Modifier.padding(top = 32.dp))
        }
    }
}

