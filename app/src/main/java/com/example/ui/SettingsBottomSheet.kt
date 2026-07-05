package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    viewModel: TodoViewModel,
    onDismiss: () -> Unit,
    onManageCategories: () -> Unit
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val geminiApiKey by viewModel.geminiApiKey.collectAsState()
    val selectedAiModel by viewModel.selectedAiModel.collectAsState()
    val availableModels by viewModel.availableAiModels.collectAsState()
    val isProcessing by viewModel.isAiProcessing.collectAsState()
    
    var tempApiKey by remember { mutableStateOf(geminiApiKey) }
    var isModelMenuExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = if (isDarkTheme) Color(0xFF0F172A) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDarkTheme) Color.White else Color.Black
            )

            // Theme Toggle
            SettingsRow(
                icon = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                title = stringResource(R.string.change_theme),
                isDarkTheme = isDarkTheme,
                action = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() }
                    )
                }
            )

            // Language Toggle
            SettingsRow(
                icon = Icons.Rounded.Language,
                title = stringResource(R.string.app_language),
                subtitle = if (appLanguage == "fa") "فارسی" else "English",
                isDarkTheme = isDarkTheme,
                onClick = {
                    val nextLang = if (appLanguage == "fa") "en" else "fa"
                    viewModel.setLanguage(nextLang)
                }
            )

            // Category Management
            SettingsRow(
                icon = Icons.Rounded.Dashboard,
                title = stringResource(R.string.manage_categories),
                isDarkTheme = isDarkTheme,
                onClick = onManageCategories
            )

            // Backup & Restore
            val context = LocalContext.current
            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { r -> r.readText() }
                    if (content != null) {
                        viewModel.importBackup(content)
                    }
                }
            }

            SettingsRow(
                icon = Icons.Rounded.CloudUpload,
                title = stringResource(R.string.export_backup),
                isDarkTheme = isDarkTheme,
                onClick = { viewModel.exportBackup() }
            )

            SettingsRow(
                icon = Icons.Rounded.CloudDownload,
                title = stringResource(R.string.import_backup),
                isDarkTheme = isDarkTheme,
                onClick = { filePickerLauncher.launch("application/json") }
            )

            // Gemini API Settings
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Gemini AI Config",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                }
                
                OutlinedTextField(
                    value = tempApiKey,
                    onValueChange = { 
                        tempApiKey = it
                        viewModel.setGeminiApiKey(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter Gemini API key") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDarkTheme) Color.White else Color.Black,
                        unfocusedTextColor = if (isDarkTheme) Color.White else Color.Black
                    )
                )

                // Model Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { isModelMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = availableModels.isNotEmpty()
                        ) {
                            Text(
                                text = if (availableModels.isEmpty()) "Load models first" else selectedAiModel,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        
                        DropdownMenu(
                            expanded = isModelMenuExpanded,
                            onDismissRequest = { isModelMenuExpanded = false },
                            modifier = Modifier.background(if (isDarkTheme) Color(0xFF1E293B) else Color.White)
                        ) {
                            availableModels.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model) },
                                    onClick = {
                                        viewModel.setAiModel(model)
                                        isModelMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.loadAvailableModels() },
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isProcessing && tempApiKey.isNotBlank()
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text("Fetch")
                        }
                    }
                }
                
                Text(
                    text = stringResource(R.string.api_key_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    isDarkTheme: Boolean,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            if (subtitle != null) {
                Text(
                    text = subtitle, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                )
            }
        }
        if (action != null) {
            action()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight, 
                contentDescription = null, 
                tint = if (isDarkTheme) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.3f)
            )
        }
    }
}
