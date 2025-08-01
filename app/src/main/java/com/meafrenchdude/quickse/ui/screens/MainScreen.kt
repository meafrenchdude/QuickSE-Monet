package com.meafrenchdude.quickse.ui.screens

import com.meafrenchdude.quickse.ui.components.ScheduledAutoToggleSwitch
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meafrenchdude.quickse.SelinuxViewModel
import com.meafrenchdude.quickse.ui.components.AboutDialog
import com.meafrenchdude.quickse.ui.components.ActionButton
import com.meafrenchdude.quickse.ui.components.AppBar
import com.meafrenchdude.quickse.ui.components.AutoToggleSwitch
import com.meafrenchdude.quickse.ui.components.StatusCard
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.meafrenchdude.quickse.ui.components.KernelInfoCardButton
import com.meafrenchdude.quickse.ui.components.NotificationToggleSwitch
import kotlinx.coroutines.launch
import android.content.Intent
import com.meafrenchdude.quickse.ui.components.FooterLinks
import androidx.core.net.toUri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun MainScreen(navController: NavController) {
    val viewModel: SelinuxViewModel = viewModel()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }
    val currentStatus = uiState.status
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkRootAccess()
        viewModel.getSelinuxStatus()
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "N/A"
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                onAboutClick = { showAboutDialog = true },
                onRefreshClick = {
                    scope.launch {
                        isRefreshing = true
                        viewModel.checkRootAccess()
                        viewModel.getSelinuxStatus()
                        delay(800)
                        isRefreshing = false
                    }
                },
                isRefreshing = isRefreshing
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .safeDrawingPadding()
                    .padding(horizontal = 24.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StatusCard(currentStatus = currentStatus)
                Spacer(modifier = Modifier.height(48.dp))
                ActionButton(
                    currentStatus = currentStatus,
                    isRootAvailable = uiState.isRootAvailable,
                    onClick = { viewModel.toggleSelinuxMode() }
                )
                AutoToggleSwitch()
                ScheduledAutoToggleSwitch()
                NotificationToggleSwitch()
                KernelInfoCardButton(
                    onClick = {
                        if (currentStatus == "Enforcing") {
                            Toast.makeText(
                                context,
                                "Please switch to permissive mode to use this feature.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            navController.navigate("kernel_info")
                        }
                    }
                )
                FooterLinks(
                    onGitHubClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/meafrenchdude/QuickSE-Monet".toUri()
                        )
                        context.startActivity(intent)
                    },
                    onTelegramClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://t.me/QuickSE_Monet".toUri()
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }
    )
    if (showAboutDialog) {
        AboutDialog(
            versionName = versionName ?: "Unknown",
            onDismiss = { showAboutDialog = false },
        )
    }
}

