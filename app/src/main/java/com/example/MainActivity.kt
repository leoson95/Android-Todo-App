package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainTodoScreen
import com.example.ui.ReminderManagementScreen
import com.example.ui.TodoViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val todoViewModel: TodoViewModel = viewModel()
            val isDarkTheme by todoViewModel.isDarkTheme.collectAsState()
            val appLanguage by todoViewModel.appLanguage.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val context = LocalContext.current
                
                val layoutDirection = androidx.compose.runtime.remember(context, appLanguage) {
                    val targetLang = if (appLanguage == "system") {
                        java.util.Locale.getDefault().language
                    } else {
                        appLanguage
                    }
                    val locale = java.util.Locale.forLanguageTag(targetLang)
                    java.util.Locale.setDefault(locale)
                    @Suppress("DEPRECATION")
                    val config = android.content.res.Configuration(context.resources.configuration)
                    config.setLocale(locale)
                    config.setLayoutDirection(locale)
                    @Suppress("DEPRECATION")
                    context.resources.updateConfiguration(config, context.resources.displayMetrics)
                    
                    if (targetLang == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr
                }

                CompositionLocalProvider(
                    LocalLayoutDirection provides layoutDirection
                ) {
                    // Dangerous runtime notifications permission check for Android 13+
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { _ -> }

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            
                            if (!hasPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "main") {
                            composable("main") {
                                MainTodoScreen(
                                    viewModel = todoViewModel,
                                    onNavigateToReminders = {
                                        navController.navigate("reminders")
                                    }
                                )
                            }
                            composable("reminders") {
                                ReminderManagementScreen(
                                    viewModel = todoViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
