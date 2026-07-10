package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Performance-optimized glass card modifier.
 * Removed 'composed' and 'shadow' to eliminate heavy GPU overdraw during scrolling.
 */
fun Modifier.glassCard(
    isDarkTheme: Boolean,
    shape: Shape = RoundedCornerShape(12.dp)
): Modifier {
    val backgroundColor = if (isDarkTheme) {
        Color(0xFF0F172A).copy(alpha = 0.65f)
    } else {
        Color.White.copy(alpha = 0.75f)
    }
    
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.15f)
    } else {
        Color.White.copy(alpha = 0.4f)
    }

    return this
        .clip(shape)
        .background(backgroundColor)
        .border(1.dp, borderColor, shape)
}

/**
 * Performance-optimized glass FAB modifier.
 */
fun Modifier.glassFab(
    isDarkTheme: Boolean
): Modifier {
    val backgroundColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.1f)
    } else {
        Color.White.copy(alpha = 0.3f)
    }
    
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.4f)
    } else {
        Color.White.copy(alpha = 0.6f)
    }

    return this
        .clip(CircleShape)
        .background(backgroundColor)
        .border(1.5.dp, borderColor, CircleShape)
}
