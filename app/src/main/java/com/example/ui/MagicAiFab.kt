package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.util.SoundManager

@Composable
fun MagicAiFab(
    onNormalClick: () -> Unit,
    onAiInput: (String) -> Unit,
    isProcessing: Boolean
) {
    var isLongPressed by remember { mutableStateOf(false) }
    var aiText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Glow Animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(16.dp)
        ) {
            // Floating Input Field (Glassmorphism)
            AnimatedVisibility(
                visible = isLongPressed,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .width(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(8.dp)
                        .glassCard(shape = RoundedCornerShape(20.dp), isDarkTheme = true)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = aiText,
                            onValueChange = { aiText = it },
                            placeholder = { Text(stringResource(R.string.ai_task_hint), fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (aiText.isNotBlank()) {
                                    onAiInput(aiText)
                                    aiText = ""
                                    isLongPressed = false
                                }
                            })
                        )
                        IconButton(onClick = {
                            if (aiText.isNotBlank()) {
                                onAiInput(aiText)
                                aiText = ""
                                isLongPressed = false
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // The FAB
            Box(contentAlignment = Alignment.Center) {
                // Glow Background
                if (isLongPressed) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(glowScale)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { 
                                    if (isLongPressed) {
                                        isLongPressed = false
                                    } else {
                                        onNormalClick() 
                                    }
                                },
                                onLongPress = {
                                    SoundManager.playTap()
                                    isLongPressed = true
                                }
                            )
                        }
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Crossfade(targetState = isLongPressed, label = "icon") { long ->
                            Icon(
                                imageVector = if (long) Icons.Default.AutoAwesome else Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
