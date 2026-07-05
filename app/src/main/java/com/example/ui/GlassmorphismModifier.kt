package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow

fun Modifier.glassCard(
    shape: Shape = RoundedCornerShape(20.dp),
    isDarkTheme: Boolean
): Modifier = composed {
    // Independent of system theme, using provided isDarkTheme state
    val backgroundColor = if (isDarkTheme) {
        Color(0xFF0F172A).copy(alpha = 0.85f) // Slate-900 (approx) with 85% alpha
    } else {
        Color.White.copy(alpha = 0.95f) // Bright White with 95% alpha
    }
    
    val borderColor = if (isDarkTheme) {
        Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
        )
    } else {
        Brush.linearGradient(
            listOf(Color.White, Color.White.copy(alpha = 0.4f))
        )
    }

    this
        .shadow(
            elevation = if (isDarkTheme) 14.dp else 4.dp,
            shape = shape,
            clip = false,
            ambientColor = Color.Black.copy(alpha = 0.2f),
            spotColor = Color.Black.copy(alpha = 0.2f)
        )
        .clip(shape)
        .background(backgroundColor)
        .border(1.2.dp, borderColor, shape)
}
