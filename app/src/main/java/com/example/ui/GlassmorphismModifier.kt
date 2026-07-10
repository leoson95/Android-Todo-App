package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
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
    val backgroundColor = if (isDarkTheme) {
        Color(0xFF0F172A).copy(alpha = 0.65f)
    } else {
        Color.White.copy(alpha = 0.75f)
    }
    
    val borderColor = if (isDarkTheme) {
        Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
        )
    } else {
        Brush.linearGradient(
            listOf(Color.White, Color.White.copy(alpha = 0.3f))
        )
    }

    this
        .clip(shape)
        .background(backgroundColor)
        .border(1.dp, borderColor, shape)
}

fun Modifier.glassFab(
    isDarkTheme: Boolean
): Modifier = composed {
    val backgroundColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.1f)
    } else {
        Color.White.copy(alpha = 0.3f)
    }
    
    val borderColor = if (isDarkTheme) {
        Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
        )
    } else {
        Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.2f))
        )
    }

    this
        .shadow(
            elevation = 16.dp,
            shape = CircleShape,
            clip = false,
            ambientColor = Color.Black.copy(alpha = 0.25f),
            spotColor = Color.Black.copy(alpha = 0.25f)
        )
        .clip(CircleShape)
        .background(backgroundColor)
        .border(1.5.dp, borderColor, CircleShape)
}
