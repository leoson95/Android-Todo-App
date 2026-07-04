package com.example.ui

import androidx.compose.foundation.background
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    
    var tempApiKey by remember { mutableStateOf(geminiApiKey) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
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
                fontWeight = FontWeight.ExtraBold
            )

            // Theme Toggle
            SettingsRow(
                icon = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                title = stringResource(R.string.change_theme),
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
                onClick = {
                    val nextLang = if (appLanguage == "fa") "en" else "fa"
                    viewModel.setLanguage(nextLang)
                }
            )

            // Category Management
            SettingsRow(
                icon = Icons.Rounded.Dashboard,
                title = stringResource(R.string.manage_categories),
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
                onClick = { viewModel.exportBackup() }
            )

            SettingsRow(
                icon = Icons.Rounded.CloudDownload,
                title = stringResource(R.string.import_backup),
                onClick = { filePickerLauncher.launch("application/json") }
            )

            // Gemini API Key
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Gemini API Key",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                OutlinedTextField(
                    value = tempApiKey,
                    onValueChange = { 
                        tempApiKey = it
                        viewModel.setGeminiApiKey(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your Gemini API key") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Text(
                    text = stringResource(R.string.api_key_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
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
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (action != null) {
            action()
        } else if (onClick != null) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
