package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow

fun Modifier.glassCard(
    shape: Shape = RoundedCornerShape(16.dp),
    isDarkTheme: Boolean
): Modifier = composed {
    val backgroundColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.05f)
    } else {
        Color.White.copy(alpha = 0.45f)
    }
    
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.1f)
    } else {
        Color.White.copy(alpha = 0.6f)
    }

    this
        .shadow(
            elevation = 8.dp,
            shape = shape,
            clip = false,
            ambientColor = Color.Black.copy(alpha = 0.05f),
            spotColor = Color.Black.copy(alpha = 0.05f)
        )
        .clip(shape)
        .background(backgroundColor)
        .border(1.dp, borderColor, shape)
}
