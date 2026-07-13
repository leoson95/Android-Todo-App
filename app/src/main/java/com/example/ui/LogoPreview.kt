package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = false, widthDp = 512, heightDp = 512)
@Composable
fun AppLogoPreview() {
    Box(
        modifier = Modifier
            .size(512.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        // Background Mesh-like circles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                    )
                )
        )
        
        // Glass effect overlay
        Surface(
            modifier = Modifier
                .size(320.dp),
            shape = RoundedCornerShape(80.dp),
            color = Color.White.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(4.dp, Color.White.copy(alpha = 0.4f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                    tint = Color.White
                )
            }
        }
    }
}
