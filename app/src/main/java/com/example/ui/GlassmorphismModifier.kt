package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow

fun Modifier.glassCard(
    shape: Shape = RoundedCornerShape(20.dp),
    isDarkTheme: Boolean
): Modifier = composed {
    val backgroundColor = if (isDarkTheme) {
        Color(0xFF1E293B).copy(alpha = 0.4f) // Slate 800 with transparency
    } else {
        Color.White.copy(alpha = 0.65f)
    }
    
    val borderColor = if (isDarkTheme) {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.15f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.8f),
                Color.White.copy(alpha = 0.2f)
            )
        )
    }

    this
        .graphicsLayer {
            // Apply slight blur to the content behind if needed, 
            // but usually we want it on the container itself
        }
        .shadow(
            elevation = if (isDarkTheme) 20.dp else 12.dp,
            shape = shape,
            clip = false,
            ambientColor = Color.Black.copy(alpha = 0.1f),
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
        .clip(shape)
        .background(backgroundColor)
        .border(1.5.dp, borderColor, shape)
        .drawWithContent {
            drawContent()
            // Optional: Add a subtle noise or grain texture if desired
        }
}
