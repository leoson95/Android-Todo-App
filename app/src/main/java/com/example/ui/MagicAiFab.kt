package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    isProcessing: Boolean,
    isDarkTheme: Boolean
) {
    var isLongPressed by remember { mutableStateOf(false) }
    var aiText by remember { mutableStateOf("") }

    // Smaller FAB (48dp) and moved significantly higher (bottom = 80dp)
    val fabSize = 48.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Outside tap detection to close
        if (isLongPressed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isLongPressed = false
                    }
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp) // Significantly higher position
                .imePadding()
        ) {
            // Floating Input Field (Enhanced Glassmorphism)
            AnimatedVisibility(
                visible = isLongPressed,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .width(320.dp)
                        .glassCard(shape = RoundedCornerShape(28.dp), isDarkTheme = isDarkTheme)
                        .padding(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = aiText,
                            onValueChange = { aiText = it },
                            placeholder = { 
                                Text(
                                    stringResource(R.string.ai_task_hint), 
                                    fontSize = 13.sp,
                                    color = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f)
                                ) 
                            },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                color = if (isDarkTheme) Color.White else Color.Black
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = if (isDarkTheme) Color.Black.copy(alpha = 0.3f) else Color.White,
                                unfocusedContainerColor = if (isDarkTheme) Color.Black.copy(alpha = 0.2f) else Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(24.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (aiText.isNotBlank()) {
                                    onAiInput(aiText)
                                    aiText = ""
                                    isLongPressed = false
                                }
                            })
                        )
                        IconButton(
                            onClick = {
                                if (aiText.isNotBlank()) {
                                    onAiInput(aiText)
                                    aiText = ""
                                    isLongPressed = false
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // The FAB (Glassy style)
            Box(
                modifier = Modifier
                    .size(fabSize)
                    .clip(RoundedCornerShape(14.dp)) // Slightly more square/modern
                    .glassCard(shape = RoundedCornerShape(14.dp), isDarkTheme = isDarkTheme)
                    .background(
                        Brush.linearGradient(
                            colors = if (isDarkTheme) {
                                listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
                            } else {
                                listOf(Color(0xFF818CF8), Color(0xFF6366F1))
                            }
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { 
                                if (isLongPressed) isLongPressed = false else onNormalClick() 
                            },
                            onLongPress = {
                                SoundManager.playTap()
                                isLongPressed = true
                            }
                        )
                    }
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Crossfade(targetState = isLongPressed, label = "icon") { long ->
                        Icon(
                            imageVector = if (long) Icons.Default.AutoAwesome else Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
